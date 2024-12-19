package com.sbp.ollamaExemple.repository;

import com.sbp.ollamaExemple.entity.ChatResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatResponseRepository extends JpaRepository<ChatResponseEntity, Long> {
}
