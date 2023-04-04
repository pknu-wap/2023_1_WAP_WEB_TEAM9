package com.example.demo.domain.tag.dto;

import com.example.demo.domain.tag.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagResponse {

    private Long id;

    private String name;

    public static List<TagResponse> listOf(List<Tag> tags) {
        return tags.stream()
            .map(TagResponse::of)
            .collect(Collectors.toList());
    }

    private static TagResponse of(Tag tag) {
        return TagResponse.builder()
            .id(tag.getId())
            .name(tag.getName())
            .build();
    }
}
