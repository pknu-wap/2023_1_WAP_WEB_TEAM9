package com.example.demo.domain.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import com.example.demo.config.JpaAuditingConfig;
import com.example.demo.domain.board.dto.BoardRequest;
import com.example.demo.domain.tag.BoardTag;
import com.example.demo.domain.tag.Tag;
import com.example.demo.repository.BoardRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(JpaAuditingConfig.class)
@DataJpaTest
class BoardTest {

    @Autowired
    private BoardRepository boardRepository;

    private BoardRequest request;

    private Board board;

    @BeforeEach
    void init() {
        board = Board.builder()
            .title("안녕하세요")
            .content("게시물 입니다.")
            .views(0L)
            .build();

        request = BoardRequest.builder()
            .title("test")
            .content("hello")
            .build();
    }

    @DisplayName("요청을 받아 게시판을 생성한다.")
    @Test
    void toBoard() {
        Board board = boardRepository.save(request.toBoard());
        assertAll(
            () -> assertThat(board.getCreateAt()).isNotNull(),
            () -> assertThat(board.getTitle()).isEqualTo(request.getTitle()),
            () -> assertThat(board.getContent()).isEqualTo(request.getContent())
        );
        System.out.println("board.getCreateAt() = " + board.getCreateAt());
    }

    @DisplayName("게시판 조회시 조회수가 증가한다.")
    @Test
    void increaseView() {
        Board board = request.toBoard();
        board.setViews(0L);

        board.increaseViews();

        assertThat(board.getViews()).isEqualTo(1L);
    }

    @DisplayName("게시판을 요청에 따라서 업데이트한다.")
    @MethodSource("generateBoardValues")
    @ParameterizedTest
    void update(BoardRequest updateRequest) {
        Board board = boardRepository.save(request.toBoard());
        board.update(updateRequest.toBoard());

        assertAll(
            () -> assumingThat(updateRequest.getContent() != null,
                () -> assertThat(updateRequest.getContent()).isEqualTo(board.getContent())),
            () -> assumingThat(updateRequest.getTitle() != null,
                () -> assertThat(updateRequest.getTitle()).isEqualTo(board.getTitle())),
            () -> assertThat(board.getUpdateAt()).isNotNull()
        );
    }

    @DisplayName("게시판에 해시태그를 추가해서 내용을 확인")
    @Test
    void addBoardTag() {
        Tag tag1 = Tag.builder().id(1L).name("인생").build();
        Tag tag2 = Tag.builder().id(2L).name("돈").build();

        BoardTag.associate(board, tag1);
        BoardTag.associate(board, tag2);

        Set<BoardTag> boardTags = board.getBoardTags();
        assertThat(boardTags.stream().map(BoardTag::getTag).map(Tag::getName))
            .contains("인생", "돈");
    }

    @DisplayName("게시판의 태그를 삭제한다.")
    @Test
    void deleteBoardTag() {
        Tag tag = new Tag(1L, "인생");
        BoardTag boardTag = BoardTag.associate(board, tag);
        board.deleteBoardTag(boardTag);

        assertThat(board.getBoardTags()).isEmpty();
    }

    @DisplayName("게시판에 여러 태그를 추가한다.")
    @Test
    void addTags() {
        Tag tag1 = new Tag(1L, "인생");
        Tag tag2 = new Tag(2L, "돈");

        board.addTags(List.of(tag1, tag2));
        Set<String> tagNames = board.getBoardTags().stream()
            .map(BoardTag::getTag)
            .map(Tag::getName)
            .collect(Collectors.toSet());

        assertThat(tagNames).contains(tag1.getName(), tag2.getName());
    }

    private static Stream<Arguments> generateBoardValues() {
        return Stream.of(
            Arguments.arguments(BoardRequest.builder()
                .title("hello").content("world").build()),
            Arguments.arguments(BoardRequest.builder()
                .title("").content("world").build()),
            Arguments.arguments(BoardRequest.builder()
                .title("hello").content("").build())
        );
    }
}
