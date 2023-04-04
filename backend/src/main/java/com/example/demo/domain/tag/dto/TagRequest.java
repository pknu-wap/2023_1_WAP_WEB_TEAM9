package com.example.demo.domain.tag.dto;

import com.example.demo.domain.tag.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagRequest {

    private String name;

    public Tag toTag() {
        return Tag.builder()
            .name(name)
            .build();
    }
}
