package com.example.demo.controller;

import static com.example.demo.security.TestMemberConstant.LOGIN_ID;
import static com.example.demo.security.TestMemberConstant.NICKNAME;
import static com.example.demo.security.TestMemberConstant.PASSWORD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.dto.BoardRequest;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.Role;
import com.example.demo.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @MockBean
    private BoardService boardService;

    private MockMvc mvc;

    private Board board;

    private Member member;

    private BoardRequest request;

    private ObjectMapper objectMapper;

    @BeforeEach
    void init(WebApplicationContext context) {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build();

        request = BoardRequest.builder()
            .content("this is board")
            .title("test")
            .build();

        member = Member.builder()
            .id(1L)
            .nickname(NICKNAME)
            .loginId(LOGIN_ID)
            .password(PASSWORD)
            .role(Role.ROLE_USER)
            .build();

        board = Board.builder()
            .id(1L)
            .content("this is board")
            .title("test")
            .views(1L)
            .createAt(LocalDateTime.now())
            .build();
        board.setMember(member);

        objectMapper = new ObjectMapper();
    }

    @Test
    void getBoard() throws Exception {
        given(boardService.getBoard(any())).willReturn(board);
        mvc.perform(get("/api/board?id=1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("title", board.getTitle()).exists())
            .andExpect(jsonPath("content", board.getContent()).exists())
            .andExpect(jsonPath("createAt", board.getCreateAt()).exists())
            .andExpect(jsonPath("views", board.getViews()).exists())
            .andExpect(jsonPath("memberNickname", member.getNickname()).exists())
            .andDo(print());
    }
}
