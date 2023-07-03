package com.uynite.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.uynite.chat.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Add any custom methods for user retrieval or other operations
}
