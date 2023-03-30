package com.example.demo.domain.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import com.example.demo.config.JpaAuditingConfig;
import com.example.demo.domain.board.dto.BoardRequest;
import com.example.demo.repository.BoardRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void init() {
        request = BoardRequest.builder()
            .title("test")
            .content("hello")
            .build();
    }

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

    @Test
    void increaseView() {
        Board board = request.toBoard();
        board.setViews(0L);

        board.increaseViews();

        assertThat(board.getViews()).isEqualTo(1L);
    }

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
