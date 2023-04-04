package com.example.demo.domain.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.domain.board.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardTagTest {

    private Tag tag;

    private Board board;

    @BeforeEach
    void init() {
        tag = Tag.builder()
            .name("인생")
            .build();

        board = Board.builder()
            .title("인생에 대한 고민")
            .content("인생이란 무었인가?")
            .build();
    }

    @DisplayName("태그와 게시판을 연결한다.")
    @Test
    void associate() {
        BoardTag boardTag = BoardTag.associate(board, tag);

        assertThat(board.getBoardTags())
            .hasSize(1);
    }
}
