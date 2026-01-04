package org.skypro.projects.personaloffers.repository;

import org.skypro.projects.personaloffers.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Override
    Optional<Product> findById(UUID id);

    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Product getReferenceById(UUID id);
}
