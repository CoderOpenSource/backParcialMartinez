package com.example.examen2.usuarios.repository;

import com.example.examen2.usuarios.model.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestEntityRepository extends JpaRepository<TestEntity, Long> {
}
