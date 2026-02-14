package com.andreadelorenzis.productivityApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.andreadelorenzis.productivityApp.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByname(String name);
}
