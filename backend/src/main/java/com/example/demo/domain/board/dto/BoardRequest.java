package com.example.demo.domain.board.dto;

import com.example.demo.domain.board.Board;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardRequest {

    private String title;
    private String content;

    public Board toBoard() {
        return Board.builder()
            .content(content)
            .title(title)
            .build();
    }
}
