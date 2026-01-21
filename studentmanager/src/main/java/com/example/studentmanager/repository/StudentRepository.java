package com.example.studentmanager.repository;

import com.example.studentmanager.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    // JpaRepository đã cung cấp các method cơ bản:
    // findAll(), findById(), save(), delete(), etc.
}
