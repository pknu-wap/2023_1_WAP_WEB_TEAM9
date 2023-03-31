package com.example.demo.controller;

import com.example.demo.domain.board.dto.BoardRequest;
import com.example.demo.domain.board.dto.BoardResponse;
import com.example.demo.domain.member.Member;
import com.example.demo.security.LoginMember;
import com.example.demo.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/{id}")
    public BoardResponse getBoard(@PathVariable Long id) {
        return BoardResponse.of(boardService.getBoard(id));
    }

    @PostMapping
    public BoardResponse registerBoard(@LoginMember Member member, @RequestBody BoardRequest request) {
        return BoardResponse.of(boardService.register(member, request));
    }

    @PatchMapping("/{id}")
    public void updateBoard(@LoginMember Member member, @PathVariable Long id, @RequestBody BoardRequest request) {
        boardService.update(member, id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteBoard(@LoginMember Member member, @PathVariable Long id) {
        boardService.delete(member, id);
    }
}
