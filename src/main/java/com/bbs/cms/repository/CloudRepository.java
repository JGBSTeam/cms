package com.bbs.cms.repository;

import com.bbs.cms.entity.Cloud;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * UserRepository
 */
@Repository
public interface CloudRepository extends CrudRepository<Cloud, Integer> {
    public Iterable<Cloud> findByUsername(String username);
    public boolean existsByCloudname(String cloudname);
    public boolean existsByPort(int port);
}