package com.example.panelplus.repository;

import com.example.panelplus.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {

    List<Menu> findByParentId(UUID parentId);

    List<Menu> findAllByOrderByWeightAsc();
}