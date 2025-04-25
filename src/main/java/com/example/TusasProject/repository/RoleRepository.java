package com.example.TusasProject.repository;

import com.example.TusasProject.entity.Role;
import com.example.TusasProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

    public interface RoleRepository extends JpaRepository<Role, Long> {
    }

