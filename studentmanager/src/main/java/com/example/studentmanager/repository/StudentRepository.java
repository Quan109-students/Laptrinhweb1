package com.example.studentmanager.repository;

import com.example.studentmanager.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    // JpaRepository đã cung cấp các method cơ bản:
    // findAll(), findById(), save(), delete(), etc.
    
    // Tìm kiếm sinh viên theo tên (không phân biệt hoa thường) - dùng native query cho SQL Server
    @Query(value = "SELECT * FROM dbo.sinhvien WHERE LOWER(FullName) LIKE LOWER(CONCAT('%', :name, '%'))", nativeQuery = true)
    List<Student> findByFullNameContainingIgnoreCase(@Param("name") String name);
    
    // Tìm kiếm sinh viên theo ID
    @Query(value = "SELECT * FROM dbo.sinhvien WHERE StudentID = :id", nativeQuery = true)
    Optional<Student> findByIdNative(@Param("id") Integer id);
    
    // Lấy ID lớn nhất
    @Query(value = "SELECT MAX(StudentID) FROM dbo.sinhvien", nativeQuery = true)
    Integer findMaxId();
    
    // Lấy tất cả sinh viên có ID lớn hơn một giá trị, sắp xếp giảm dần
    @Query(value = "SELECT * FROM dbo.sinhvien WHERE StudentID > :id ORDER BY StudentID DESC", nativeQuery = true)
    List<Student> findAllByIdGreaterThanOrderByIdDesc(@Param("id") Integer id);
    
    // Cập nhật ID của sinh viên
    @Modifying
    @Transactional
    @Query(value = "UPDATE dbo.sinhvien SET StudentID = :newId WHERE StudentID = :oldId", nativeQuery = true)
    void updateStudentId(@Param("oldId") Integer oldId, @Param("newId") Integer newId);
}
