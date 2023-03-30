package com.example.demo.service;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.dto.BoardRequest;
import com.example.demo.domain.member.Member;
import com.example.demo.repository.BoardRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public Board getBoard(Long boardId) {
        Board board = boardRepository.findBoardById(boardId).orElseThrow(EntityNotFoundException::new);
        board.increaseViews();
        return board;
    }

    @Transactional
    public void update(Member member, Long boardId, BoardRequest request) {
        Board board = boardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        if (!board.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("게시판 주인이 아닙니다.");
        }
        board.update(request.toBoard());
    }

    @Transactional
    public Board register(Member member, BoardRequest request) {
        Board board = request.toBoard();
        board.setMember(member);
        return boardRepository.save(board);
    }

    @Transactional
    public void delete(Member member, Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(EntityNotFoundException::new);
        if (!board.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("게시판 주인이 아닙니다.");
        }
        member.deleteBoard(board);
        boardRepository.delete(board);
    }
}
