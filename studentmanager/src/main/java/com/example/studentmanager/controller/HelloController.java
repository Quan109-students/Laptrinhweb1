package com.example.studentmanager.controller;

import com.example.studentmanager.model.Student;
import com.example.studentmanager.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DataSource dataSource;

    // Endpoint test kết nối database
    @GetMapping("/test-connection")
    public ResponseEntity<?> testConnection() {
        Map<String, Object> result = new HashMap<>();
        try {
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            
            result.put("status", "SUCCESS");
            result.put("message", "Kết nối database thành công!");
            result.put("databaseUrl", metaData.getURL());
            result.put("databaseProductName", metaData.getDatabaseProductName());
            result.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            result.put("driverName", metaData.getDriverName());
            result.put("driverVersion", metaData.getDriverVersion());
            result.put("username", metaData.getUserName());
            
            // Test query
            long count = studentRepository.count();
            result.put("totalStudents", count);
            
            connection.close();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", "FAILED");
            result.put("message", "Không thể kết nối database");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // Bài 1
    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring Boot API";
    }

    // Bài 2
    @GetMapping("/greet")
    public String greet(@RequestParam String name) {
        return "Xin chào " + name;
    }

    // API tìm kiếm sinh viên theo tên hoặc ID
    @GetMapping("/students/search")
    public ResponseEntity<?> searchStudents(@RequestParam(required = false) String name,
                                           @RequestParam(required = false) Integer id) {
        try {
            // Nếu có ID, tìm theo ID
            if (id != null) {
                Optional<Student> student = studentRepository.findById(id);
                if (student.isPresent()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("keyword", "ID: " + id);
                    result.put("total", 1);
                    result.put("students", List.of(student.get()));
                    return ResponseEntity.ok(result);
                } else {
                    Map<String, Object> result = new HashMap<>();
                    result.put("keyword", "ID: " + id);
                    result.put("total", 0);
                    result.put("students", List.of());
                    return ResponseEntity.ok(result);
                }
            }
            
            // Nếu có name, tìm theo tên
            if (name != null && !name.trim().isEmpty()) {
                List<Student> students = studentRepository.findByFullNameContainingIgnoreCase(name.trim());
                
                Map<String, Object> result = new HashMap<>();
                result.put("keyword", name);
                result.put("total", students.size());
                result.put("students", students);
                
                return ResponseEntity.ok(result);
            }
            
            // Nếu không có tham số nào
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Vui lòng cung cấp tham số 'name' hoặc 'id' để tìm kiếm"));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi tìm kiếm sinh viên: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // API lấy sinh viên theo ID
    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable int id) {
        try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isPresent()) {
                return ResponseEntity.ok(student.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy sinh viên với ID: " + id));
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi truy vấn database: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Bài 4 - Lấy một sinh viên đầu tiên từ database
    @GetMapping("/student")
    public ResponseEntity<?> getStudent() {
        try {
            List<Student> students = studentRepository.findAll();
            if (!students.isEmpty()) {
                return ResponseEntity.ok(students.get(0));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không có dữ liệu sinh viên trong database"));
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi truy vấn database: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // API lấy danh sách tất cả sinh viên (Get All)
    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = studentRepository.findAll();
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", students.size());
            result.put("students", students);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Không thể kết nối database: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Thêm sinh viên mới
    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (student.getFullName() == null || student.getFullName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Họ và tên không được để trống"));
            }

            // Không set ID - để SQL Server tự động tạo (IDENTITY column)
            student.setStudentID(null);

            Student savedStudent = studentRepository.save(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi thêm sinh viên: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Cập nhật sinh viên
    @PutMapping("/students/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable int id, @RequestBody Student student) {
        try {
            Optional<Student> existingStudent = studentRepository.findById(id);
            if (existingStudent.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy sinh viên với ID: " + id));
            }

            // Kiểm tra dữ liệu đầu vào
            if (student.getFullName() == null || student.getFullName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Họ và tên không được để trống"));
            }

            Student studentToUpdate = existingStudent.get();
            studentToUpdate.setFullName(student.getFullName());
            studentToUpdate.setGender(student.getGender());
            studentToUpdate.setBirthDate(student.getBirthDate());
            studentToUpdate.setMajor(student.getMajor());
            studentToUpdate.setAddress(student.getAddress());

            Student updatedStudent = studentRepository.save(studentToUpdate);
            return ResponseEntity.ok(updatedStudent);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi cập nhật sinh viên: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Xóa sinh viên và tự động cập nhật ID
    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable int id) {
        try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy sinh viên với ID: " + id));
            }

            // Xóa sinh viên
            studentRepository.deleteById(id);
            
            // Tự động cập nhật ID: Tất cả sinh viên có ID > id đã xóa sẽ giảm đi 1
            // Cập nhật từ lớn xuống nhỏ để tránh conflict
            List<Student> studentsToUpdate = studentRepository.findAllByIdGreaterThanOrderByIdDesc(id);
            for (Student s : studentsToUpdate) {
                Integer oldId = s.getStudentID();
                Integer newId = oldId - 1;
                // Dùng native query để update ID trực tiếp
                studentRepository.updateStudentId(oldId, newId);
            }
            
            return ResponseEntity.ok(Map.of("message", "Đã xóa sinh viên thành công và cập nhật ID", "id", id));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi xóa sinh viên: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Xử lý exception chung
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Đã xảy ra lỗi: " + e.getMessage());
        error.put("details", e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
