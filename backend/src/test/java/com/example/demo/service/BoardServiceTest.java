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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TagService tagService;

    private Member member;

    private BoardRequest request;

    private Board board;

    @BeforeEach
    void init() {
        boardService = new BoardService(boardRepository, tagService);

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
            .tagNames(List.of("인생", "돈", "사랑"))
            .build();
    }

    @DisplayName("게시판을 조회한다.")
    @Test
    void getBoard() {
        Long views = board.getViews();
        when(boardRepository.findBoardById(any())).thenReturn(Optional.of(board));

        Board savedBoard = boardService.getBoard(board.getId());

        assertThat(savedBoard.getViews()).isEqualTo(views + 1);
        verify(boardRepository).findBoardById(any());
    }

    @DisplayName("게시판을 업데이트 한다.")
    @Test
    void update() {
        BoardRequest updateRequest = BoardRequest.builder()
            .title("hello")
            .content("updated Board")
            .tagNames(List.of("인생", "돈"))
            .build();
        when(boardRepository.findById(any())).thenReturn(Optional.of(board));

        boardService.update(member, board.getId(), updateRequest);

        assertAll(
            () -> assertThat(board.getId()).isEqualTo(1L),
            () -> assertThat(board.getContent()).isEqualTo(updateRequest.getContent()),
            () -> assertThat(board.getTitle()).isEqualTo(updateRequest.getTitle()),
            () -> assertThat(board.getViews()).isNotNull(),
            () -> assertThat(board.getBoardTags()).hasSize(2)
        );
    }

    @DisplayName("주인이 아닌 사람이 게시판 업데이트를 수행하면 예외가 발생")
    @Test
    void updateNotOwner() {
        BoardRequest updateRequest = BoardRequest.builder()
            .title("hello")
            .content("updated Board")
            .build();
        Member notOwner = getNotOwner();
        Long boardId = board.getId();

        when(boardRepository.findById(any())).thenReturn(Optional.of(board));

        assertThatThrownBy(() -> boardService.update(notOwner, boardId, updateRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("게시판을 등록한다.")
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
        Long boardId = board.getId();

        assertAll(
            () -> assertThatThrownBy(() -> boardService.delete(notOwner, boardId))
                .isInstanceOf(IllegalArgumentException.class),
            () -> verify(boardRepository, never()).delete(any())
        );
    }

    private Member getNotOwner() {
        return Member.builder()
            .loginId("notOwner123")
            .password("11111111111")
            .nickname("not Owner")
            .id(2L)
            .build();
    }
}
