package com.example.gerenciador.history.repository;

import com.example.gerenciador.history.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {

    Page<History> findAllByFamilyId(Pageable pageable, Long familyId);
    List<History> findAllByFamilyIdAndIdIn(Long familyId, List<Long> ids);

    Optional<History> findByIdAndFamilyId(Long historyId, Long familyId);
}
