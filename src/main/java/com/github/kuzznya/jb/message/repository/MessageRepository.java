package com.github.kuzznya.jb.message.repository;

import com.github.kuzznya.jb.message.entity.ScheduledMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<ScheduledMessageEntity, UUID> {
}
