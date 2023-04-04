package com.example.demo.domain;

import java.time.LocalDateTime;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@EntityListeners(value = {AuditingEntityListener.class})
@MappedSuperclass
public class BaseTimeEntity {

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    @CreatedDate
    private LocalDateTime createAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    @LastModifiedDate
    private LocalDateTime updateAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime deleteAt;
}
