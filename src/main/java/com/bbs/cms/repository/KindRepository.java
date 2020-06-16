package com.bbs.cms.repository;

import java.util.Optional;

import com.bbs.cms.entity.Kind;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KindRepository extends CrudRepository<Kind, Integer> {
    public Optional<Kind> getByKindName(String kind);
}