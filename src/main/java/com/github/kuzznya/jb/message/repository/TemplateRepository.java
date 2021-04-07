package com.github.kuzznya.jb.message.repository;

import com.github.kuzznya.jb.message.entity.MessageTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<MessageTemplateEntity, String> {
}
