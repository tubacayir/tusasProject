package com.example.TusasProject.repository;

import com.example.TusasProject.entity.Role;
import com.example.TusasProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface RoleRepository extends JpaRepository<Role, Long> {
        Optional<Role> findByName(String name);


    }

