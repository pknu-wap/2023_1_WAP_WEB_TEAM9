package com.example.demo.service;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.dto.BoardRequest;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.tag.Tag;
import com.example.demo.repository.BoardRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final TagService tagService;

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
        if (request.getTagNames() != null) {
            List<Tag> tags = getTags(request);
            board.updateTags(tags);
        }
        board.update(request.toBoard());
    }

    @Transactional
    public Board register(Member member, BoardRequest request) {
        Board board = request.toBoard();
        if (request.getTagNames() != null) {
            List<Tag> tags = getTags(request);
            board.addTags(tags);
        }
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

    private List<Tag> getTags(BoardRequest request) {
        Set<Tag> tagsByRequest = request.getTagNames().stream().map(Tag::of).collect(Collectors.toSet());
        List<Tag> tags = tagService.findAllByNameIn(request.getTagNames());
        Set<String> existingTagNames = tags.stream().map(Tag::getName).collect(Collectors.toSet());

        tagsByRequest.forEach((tag -> {
            if (!existingTagNames.contains(tag.getName())) {
                tags.add(tag);
            }
        }));
        return tags;
    }
}
