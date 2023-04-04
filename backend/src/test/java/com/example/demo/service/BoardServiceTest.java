package com.example.demo.service;

import static com.example.demo.security.TestMemberConstant.LOGIN_ID;
import static com.example.demo.security.TestMemberConstant.NICKNAME;
import static com.example.demo.security.TestMemberConstant.PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.dto.BoardRequest;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.Role;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;

    private BoardRequest request;

    private Board board;

    @BeforeEach
    void init() {
        boardService = new BoardService(boardRepository);

        member = Member.builder()
            .id(1L)
            .loginId(LOGIN_ID)
            .password(PASSWORD)
            .nickname(NICKNAME)
            .role(Role.ROLE_USER)
            .build();

        board = Board.builder()
            .id(1L)
            .views(0L)
            .content("This is test board")
            .title("board")
            .build();
        board.setMember(member);

        request = BoardRequest.builder()
            .content("This is test board")
            .title("board")
            .build();
    }

    @Test
    void getBoard() {
        Long views = board.getViews();
        when(boardRepository.findBoardById(any())).thenReturn(Optional.of(board));

        Board savedBoard = boardService.getBoard(board.getId());

        assertThat(savedBoard.getViews()).isEqualTo(views + 1);
        verify(boardRepository).findBoardById(any());
    }

    @Test
    void update() {
        BoardRequest updateRequest = BoardRequest.builder()
            .title("hello")
            .content("updated Board")
            .build();
        when(boardRepository.findById(any())).thenReturn(Optional.of(board));

        boardService.update(member, board.getId(), updateRequest);

        assertAll(
            () -> assertThat(board.getId()).isEqualTo(1L),
            () -> assertThat(board.getContent()).isEqualTo(updateRequest.getContent()),
            () -> assertThat(board.getTitle()).isEqualTo(updateRequest.getTitle()),
            () -> assertThat(board.getViews()).isNotNull());
    }

    @Test
    void updateNotOwner() {
        BoardRequest updateRequest = BoardRequest.builder()
            .title("hello")
            .content("updated Board")
            .build();
        Member notOwner = getNotOwner();

        when(boardRepository.findById(any())).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> boardService.update(notOwner, board.getId(), updateRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private Member getNotOwner() {
        return Member.builder()
            .loginId("notOwner123")
            .password("11111111111")
            .nickname("not Owner")
            .id(2L)
            .build();
    }

    @Test
    void register() {
        when(boardRepository.save(any())).thenReturn(board);

        Board register = boardService.register(member, request);

        assertAll(
            () -> assertThat(register.getMember().getId()).isEqualTo(member.getId()),
            () -> verify(boardRepository).save(any())
        );
    }

    @Test
    void delete() {
        when(boardRepository.findById(any())).thenReturn(Optional.of(board));

        boardService.delete(member, board.getId());

        assertAll(
            () -> verify(boardRepository).delete(board),
            () -> assertThat(member.getBoards()).doesNotContain(board)
        );
    }

    @Test
    void deleteBoardNotOwned() {
        when(boardRepository.findById(any())).thenReturn(Optional.of(board));
        Member notOwner = getNotOwner();

        assertAll(
            () -> assertThatThrownBy(() -> boardService.delete(notOwner, board.getId()))
                .isInstanceOf(IllegalArgumentException.class),
            () -> verify(boardRepository, never()).delete(any())
        );
    }
}
