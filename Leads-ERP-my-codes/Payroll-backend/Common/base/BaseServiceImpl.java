package com.leads.microcube.base;

import com.leads.microcube.base.command.IdHolder;
import com.leads.microcube.base.query.PageResponse;
import com.leads.model.ActionType;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;


public abstract class BaseServiceImpl<Entity extends BaseEntity, REQ extends IdHolder, RES> extends BaseServiceHelper<Entity, REQ, RES> implements BaseService<REQ, RES>, BaseQueryService<REQ, RES>{

    protected final BaseRepository<Entity> repository;

    protected BaseServiceImpl(BaseRepository<Entity> repository) {
        super();
        this.repository = repository;
    }

    @Override
    public RES create(REQ d) {
        Entity e = convertForCreate(d);
        Entity saved = repository.save(e);
        return toResponse(saved);
    }

    @Override
    public RES update(REQ d) {
        String uuid = d.getUuid();
        Entity res = null;

        // Find the existing entity by UUID (only non-deleted entities)
        Optional<Entity> entityOptional = repository.findByUuidAndIsDeletedFalse(uuid);
        if (entityOptional.isPresent()) {
            Entity entity = entityOptional.get();
            // Copy properties from DTO to existing entity
            BeanUtils.copyProperties(d, entity, "id");

            // Save the updated entity
            res = repository.save(entity);
        }
        return toResponse(res);
    }

    @Override
    public RES delete(String uuid) {
        Entity entity = null;

        // Find the entity by UUID (only non-deleted entities)
        Optional<Entity> entityOptional = repository.findByUuidAndIsDeletedFalse(uuid);
        if(entityOptional.isPresent()) {
            entity = entityOptional.get();

            // Mark entity as deleted (soft delete)
            entity.setIsDeleted(true);
            entity.setActionType(ActionType.DELETE);
            // Save the entity with updated deletion status
            repository.save(entity);
        }
        return toResponse(entity);
    }

    @Override
    public List<RES> get() {
        List<Entity> response = repository.findAllByIsDeletedFalse();
        return toResponseList(response);
    }

    @Override
    public RES get(String uuid) {
        Optional<Entity> entityOptional = repository.findByUuidAndIsDeletedFalse(uuid);
        Entity entity = null;
        if(entityOptional.isPresent()) {
            entity = entityOptional.get();
        }
        return toResponse(entity);
    }

    @Override
    public RES get(Long id) {
        Optional<Entity> entityOptional = repository.findByIdAndIsDeletedFalse(id);
        Entity entity = null;
        if(entityOptional.isPresent()) {
            entity = entityOptional.get();
        }
        return toResponse(entity);
    }

    @Override
    public PageResponse<RES> get(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("id").descending());
        Page<Entity> page = repository.findAllByIsDeletedFalse(pageable);
        return toPageResponseFromEntities(page);
    }

    protected PageResponse<RES> toPageResponseFromEntities(Page<Entity> entityPage) {
        Page<RES> mapped = entityPage.map(this::toResponse);
        return toPageResponse(mapped);
    }

}
