package com.example.TusasProject.repository;


import com.example.TusasProject.entity.Opinion;
import com.example.TusasProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpinionRepository extends JpaRepository<Opinion, Long> {


}
