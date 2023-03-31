package com.example.demo.domain.board.dto;

import com.example.demo.domain.board.Board;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardResponse {

    private Long id;
    private String title;
    private String content;
    private Long views;
    private String memberNickname;
    private LocalDateTime createAt;

    public static BoardResponse of(Board board) {
        return BoardResponse.builder()
            .id(board.getId())
            .title(board.getTitle())
            .content(board.getContent())
            .views(board.getViews())
            .memberNickname(board.getMember().getNickname())
            .createAt(board.getCreateAt())
            .build();
    }
}
