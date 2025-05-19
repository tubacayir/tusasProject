package com.example.TusasProject.repository;

import com.example.TusasProject.entity.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {
}