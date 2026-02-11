package com.example.panelplus.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.StreamSupport;

public abstract class BaseDtoService<E, ID, Req, Res, R extends JpaRepository<E, ID>> extends BaseService<E, ID, R> {

    protected BaseDtoService(R repo) {
        super(repo);
    }

    protected abstract E toEntity(Req dto);

    protected abstract Res toResponse(E entity);

    protected abstract void updateEntity(E entity, Req dto);

    protected abstract RuntimeException notFound(ID id);

    public Res create(Req dto) {
        E entity = toEntity(dto);
        entity = save(entity);
        return toResponse(entity);
    }

    public Res update(ID id, Req dto) {
        E entity = findById(id);
        if (entity == null) throw notFound(id);

        updateEntity(entity, dto);
        entity = save(entity);

        return toResponse(entity);
    }

    public Res get(ID id) {
        E entity = findById(id);
        if (entity == null) throw notFound(id);
        return toResponse(entity);
    }

    public List<Res> getAllDto() {
        return StreamSupport.stream(findAll().spliterator(), false)
                .map(this::toResponse)
                .toList();
    }
}

