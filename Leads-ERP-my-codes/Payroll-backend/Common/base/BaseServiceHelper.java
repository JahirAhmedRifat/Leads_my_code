package com.leads.microcube.base;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.leads.microcube.base.Exception.CustomException;
import com.leads.microcube.base.query.PageResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class BaseServiceHelper<E, D, RS> {
    private static final ModelMapper modelMapper = new ModelMapper();
    ObjectMapper objectMapper = new ObjectMapper();
    protected E convertForCreate(D d) {
        return modelMapper.map(d, getEntityClass());
    }

    protected RS toResponse(E entity) {
        if(entity != null)
            return modelMapper.map(entity, getResponseClass());
        return null;
    }

    protected List<RS> toResponseList(List<E> entities) {
        if(!entities.isEmpty())
            return entities.stream()
                .map(this::toResponse)
                .toList();
        return List.of();
    }

    protected String toTargetString(Object obj) {
        try{
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(obj);
        }catch (JsonProcessingException ex){
           throw new CustomException(HttpStatus.CONFLICT, "");
        }
    }

    public Class<E> getEntityClass() {
        return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private Class<D> getDtoClass() {
        return (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    private Class<RS> getResponseClass() {
        return (Class<RS>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
    }

    protected <e, d> d convertToTarget(e entity, Class<d> targetTypeClass) {
        if (entity == null) return null;
        return modelMapper.map(entity, targetTypeClass);
    }

    protected <e, d> List<d> convertListToTargetList(List<e> eList, Class<d> targetTypeClass) {
        ParameterizedType targetType = new ListParameterizedType(targetTypeClass);
        return modelMapper.map(eList, targetType);
    }

    protected <T> List<T> convertPayloadToResponeList(Object payload, Class<T> clazz) {
        if (payload == null) {
            return Collections.emptyList();
        }
        return objectMapper.convertValue(
                payload,
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, clazz)
        );
    }

    public <T, K> Map<K, List<T>> groupListByProperty(List<T> list, Function<T, K> propertyExtractor) {
        return list.stream().collect(Collectors.groupingBy(propertyExtractor));
    }

    protected PageResponse<RS> toPageResponse(Page<RS> springPage) {
        return new PageResponse<>(
                springPage.getContent(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.isFirst(),
                springPage.isLast(),
                springPage.hasNext(),
                springPage.hasPrevious()
        );
    }

}
