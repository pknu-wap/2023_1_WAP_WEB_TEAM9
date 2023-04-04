package com.example.demo.controller;

import static com.example.demo.security.TestMemberConstant.LOGIN_ID;
import static com.example.demo.security.TestMemberConstant.NICKNAME;
import static com.example.demo.security.TestMemberConstant.PASSWORD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.common.WithCustomMember;
import com.example.demo.config.security.AuthProperties;
import com.example.demo.config.security.WebSecurityConfig;
import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.dto.BoardRequest;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.Role;
import com.example.demo.security.MemberDetailsService;
import com.example.demo.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Import(WebSecurityConfig.class)
@ComponentScan(basePackages = "com.example.demo.security")
@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @MockBean
    private BoardService boardService;

    @MockBean
    private AuthProperties authProperties;

    @MockBean
    private MemberDetailsService service;

    private MockMvc mvc;

    private Board board;

    private Member member;

    private BoardRequest request;

    private ObjectMapper objectMapper;

    @BeforeEach
    void init(WebApplicationContext context) {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
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
            .build();
        board.setMember(member);

        objectMapper = new ObjectMapper();
    }

    @Test
    void getBoard() throws Exception {
        given(boardService.getBoard(any())).willReturn(board);
        mvc.perform(get("/api/board/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("title", board.getTitle()).exists())
            .andExpect(jsonPath("content", board.getContent()).exists())
            .andExpect(jsonPath("views", board.getViews()).exists())
            .andExpect(jsonPath("memberNickname", member.getNickname()).exists())
            .andDo(print());
        verify(boardService).getBoard(any());
    }

    @Test
    @WithCustomMember
    void registerBoard() throws Exception {
        given(boardService.register(any(), any())).willReturn(board);
        mvc.perform(post("/api/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("title", board.getTitle()).exists())
            .andExpect(jsonPath("content", board.getContent()).exists())
            .andExpect(jsonPath("views", board.getViews()).exists())
            .andExpect(jsonPath("memberNickname", member.getNickname()).exists())
            .andDo(print());
        verify(boardService).register(any(), any());
    }

    @Test
    @WithCustomMember
    public void updateBoard() throws Exception {
        BoardRequest updateRequest = BoardRequest.builder()
            .content("updated")
            .title("")
            .build();

        mvc.perform(patch("/api/board/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print());
        verify(boardService).update(any(), any(), any());
    }

    @Test
    @WithCustomMember
    public void deleteBoard() throws Exception {
        mvc.perform(delete("/api/board/1")
                .with(csrf()))
            .andExpect(status().isOk())
            .andDo(print());
        verify(boardService).delete(any(), any());
    }
}
