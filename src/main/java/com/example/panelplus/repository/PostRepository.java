package com.example.panelplus.repository;

import com.example.panelplus.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByStatus(Integer status);

    List<Post> findByUserId(Integer userId);
}