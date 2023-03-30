package com.example.demo.controller;

import com.example.demo.domain.board.dto.BoardResponse;
import com.example.demo.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public BoardResponse getBoard(@RequestParam Long id) {
        return BoardResponse.of(boardService.getBoard(id));
    }
}
