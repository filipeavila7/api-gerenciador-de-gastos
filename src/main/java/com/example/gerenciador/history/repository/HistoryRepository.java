package com.example.gerenciador.history.repository;

import com.example.gerenciador.history.entity.History;
import com.example.gerenciador.history.entity.HistoryAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {

    Page<History> findAllByFamilyId(Pageable pageable, Long familyId);
    List<History> findAllByFamilyIdAndIdIn(Long familyId, List<Long> ids);

    @Query("""
        SELECT h FROM History h
        WHERE h.family.id = :familyId
        AND (:action IS NULL OR h.action = :action)
        AND (:description IS NULL OR LOWER(h.description) LIKE LOWER(CONCAT('%', :description, '%')))
    """)
    Page<History> historySearch(
            @Param("familyId") Long familyId,
            @Param("action") HistoryAction action,
            @Param("description") String description,
            Pageable pageable
    );

    Optional<History> findByIdAndFamilyId(Long historyId, Long familyId);
}
