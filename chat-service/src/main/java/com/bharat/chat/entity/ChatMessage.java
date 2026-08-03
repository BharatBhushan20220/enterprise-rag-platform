package com.bharat.chat.entity;

import com.bharat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "chat_messages")
public class ChatMessage extends BaseEntity {

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false, length = 4000)
    private String question;

    @Column(nullable = false, length = 8000)
    private String answer;

    @Column(length = 8000)
    private String sources;
}
