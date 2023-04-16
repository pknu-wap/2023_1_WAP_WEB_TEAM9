package com.example.demo.domain.tag;

import com.example.demo.domain.BaseTimeEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(indexes = {
    @Index(columnList = "name", unique = true)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Builder
    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Tag of(String name) {
        return Tag.builder().name(name).build();
    }

    public void updateTag(String updateName) {
        this.name = updateName;
    }
}
