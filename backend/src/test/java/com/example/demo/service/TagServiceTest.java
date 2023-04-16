package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.tag.Tag;
import com.example.demo.domain.tag.dto.TagRequest;
import com.example.demo.repository.BoardTagRepository;
import com.example.demo.repository.TagRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    private TagService tagService;

    @Mock
    private BoardTagRepository boardTagRepository;

    @Mock
    private TagRepository tagRepository;

    @BeforeEach
    void init() {
        tagService = new TagService(tagRepository, boardTagRepository);
    }

    @DisplayName("태그를 저장한다.")
    @Test
    void saveTag() {
        TagRequest request = TagRequest.builder()
            .name("인생")
            .build();
        when(tagRepository.existsByName(any())).thenReturn(false);

        tagService.saveTag(request);

        verify(tagRepository).save(any());
    }

    @DisplayName("같은 이름의 태그가 저장되면 예외가 발생된다.")
    @Test
    void inputDuplicateTag() {
        TagRequest request = TagRequest.builder()
            .name("인생")
            .build();
        when(tagRepository.existsByName(any())).thenReturn(true);

        assertThatThrownBy(() -> tagService.saveTag(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("인기 있는 태그을 조회한다.")
    @Test
    void findPopularTag() {
        Tag firstTag = Tag.builder()
            .name("인생")
            .build();
        Tag secondTag = Tag.builder()
            .name("돈")
            .build();
        when(boardTagRepository.findTagByCount(any())).thenReturn(List.of(firstTag, secondTag));

        List<Tag> tags = tagService.findPopularTag(2);


        assertAll(
            () -> assertThat(tags).containsExactly(firstTag, secondTag),
            () -> verify(boardTagRepository).findTagByCount(any())
        );
    }

    @DisplayName("입력된 이름의 태그 중 저장된 태그를 불러온다.")
    @Test
    void findAllByNameIn() {
        Tag tag1 = Tag.builder().name("인생").build();
        Tag tag2 = Tag.builder().name("돈").build();
        List<String> tagNames = List.of("인생", "돈", "사랑");
        when(tagRepository.findAllByNameIn(any())).thenReturn(List.of(tag1, tag2));

        List<Tag> tags = tagService.findAllByNameIn(tagNames);

        assertAll(
            () -> assertThat(tags).containsExactly(tag1, tag2),
            () -> verify(tagRepository).findAllByNameIn(any())
        );
    }
}
