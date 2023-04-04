package com.example.demo.domain.tag;

import com.example.demo.domain.BaseTimeEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    @Builder
    public Tag(Long id, String name) {
        Id = id;
        this.name = name;
    }

    public void updateTag(String updateName) {
        this.name = updateName;
    }
}
