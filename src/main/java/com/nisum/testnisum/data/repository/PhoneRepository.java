package com.nisum.testnisum.data.repository;

import com.nisum.testnisum.data.entity.Phone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhoneRepository extends CrudRepository<Phone, UUID> {
    List<Phone> findByUserId(UUID userId);
}
