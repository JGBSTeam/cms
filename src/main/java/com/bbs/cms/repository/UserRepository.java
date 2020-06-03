package com.bbs.cms.repository;

import com.bbs.cms.entity.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * UserRepository
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

  public boolean existsByUsername(String username);

}