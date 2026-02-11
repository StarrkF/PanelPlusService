package com.example.panelplus.repository;

import com.example.panelplus.entity.MenuPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuPostRepository extends JpaRepository<MenuPost, MenuPost.Pk> {

    List<MenuPost> findByMenuId(UUID menuId);

    List<MenuPost> findByPostId(UUID postId);
}
