package com.example.gerenciador.family.repository;

import com.example.gerenciador.family.entity.Family;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<Family, Long> {
    Page<Family> findAll(Pageable pageable);
}
