package com.example.panelplus.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseService<E, ID, R extends JpaRepository<E, ID>> {

    private final R repo;

    public BaseService(R repo) {
        this.repo = repo;
    }

    public final R getRepository() {
        return repo;
    }

    public E findById(ID id) {
        return repo.findById(id).orElse(null);
    }

    public Iterable<E> findAll() {
        return repo.findAll();
    }

    public Iterable<E> findAllById(Iterable<ID> ids) {
        if ( ids == null ) return null;
        return repo.findAllById(ids);
    }

    public Page<E> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public long count() {
        return repo.count();
    }

    public boolean existsById(ID id) {
        return repo.existsById(id);
    }

    public void deleteById(ID id) {
        repo.deleteById(id);
    }

    public void delete(E entity) {
        repo.delete(entity);
    }

    public void deleteAll(Iterable<? extends E> entities) {
        repo.deleteAll(entities);
    }

    public void deleteAll() {
        repo.deleteAll();
    }

    public E save(E entity) {
        return repo.save(entity);
    }

    public Iterable<E> saveAll(Iterable<E> entities) {
        return repo.saveAll(entities);
    }

}
