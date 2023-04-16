package com.example.demo.domain.board.dto;

import com.example.demo.domain.board.Board;
import java.util.List;
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
    private List<String> tagNames;

    public Board toBoard() {
        return Board.builder()
            .content(content)
            .title(title)
            .build();
    }
}
