package com.bbs.cms.repository;

import java.util.List;

import com.bbs.cms.entity.Cloud;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * UserRepository
 */
@Repository
public interface CloudRepository extends CrudRepository<Cloud, Integer> {
    public List<Cloud> findByUsername(String username);
    public boolean existsByCloudname(String cloudname);
    public boolean existsByOuterport(int port);
}