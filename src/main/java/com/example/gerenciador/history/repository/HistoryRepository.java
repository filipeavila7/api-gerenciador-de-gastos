package com.example.gerenciador.history.repository;

import com.example.gerenciador.history.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
