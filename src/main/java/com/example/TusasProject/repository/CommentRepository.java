package com.example.TusasProject.repository;


import com.example.TusasProject.entity.Comment;
import com.example.TusasProject.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}