package com.leads.microcube.base;

import com.leads.microcube.base.command.IdHolder;
import com.leads.microcube.base.query.CommonResponse;
import com.leads.microcube.base.query.Envelope;
import com.leads.microcube.helper.MessageConstant;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public abstract class BaseController<REQ extends IdHolder, RES> {

    protected final BaseService<REQ, RES> service;
    protected final BaseQueryService<REQ, RES> queryService;

    protected BaseController(BaseService<REQ, RES> service, BaseQueryService<REQ, RES> queryService) {
        this.service = service;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<Envelope> create(@Valid @RequestBody REQ request) {
        return ResponseEntity.ok(CommonResponse.makeResponse(service.create(request), MessageConstant.RESOURCE_CREATED));
    }

    @PutMapping
    public ResponseEntity<Envelope> update(@Valid @RequestBody REQ request) {
        return ResponseEntity.ok(CommonResponse.makeResponse(service.update(request), MessageConstant.RESOURCE_UPDATE));
    }

    @DeleteMapping("{uuid}")
    public ResponseEntity<Envelope> delete(@PathVariable String uuid) {
        return ResponseEntity.ok(CommonResponse.makeResponse(service.delete(uuid), MessageConstant.RESOURCE_DELETE));
    }

    @GetMapping
    public ResponseEntity<Envelope> get() {
        return ResponseEntity.ok(CommonResponse.makeResponse(queryService.get(), MessageConstant.RESOURCE_FETCH));
    }

    @GetMapping("{uuid}")
    public ResponseEntity<Envelope> get(@PathVariable String uuid) {
        return ResponseEntity.ok(CommonResponse.makeResponse(queryService.get(uuid), MessageConstant.RESOURCE_FETCH));
    }

    @GetMapping("id/{id}")
    public ResponseEntity<Envelope> get(@PathVariable Long id) {
        return ResponseEntity.ok(CommonResponse.makeResponse(queryService.get(id), MessageConstant.RESOURCE_FETCH));
    }

    @GetMapping("{pageIndex}/{pageSize}")
    public ResponseEntity<Envelope> get(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return ResponseEntity.ok(CommonResponse.makeResponse(queryService.get(pageIndex, pageSize), MessageConstant.RESOURCE_FETCH));
    }
}
