package com.example.demo.database;

import static com.example.demo.security.TestMemberConstant.LOGIN_ID;
import static com.example.demo.security.TestMemberConstant.NICKNAME;
import static com.example.demo.security.TestMemberConstant.PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.dto.BoardRequest;
import com.example.demo.domain.member.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.BoardService;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Transactional
@SpringBootTest
public class PerformanceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private BoardService boardService;

    @Autowired
    private TransactionTemplate transaction;

    private Member member;

    @BeforeEach
    void init() {
        member = Member.builder()
            .loginId(LOGIN_ID)
            .password(PASSWORD)
            .nickname(NICKNAME)
            .build();
        memberRepository.save(member);
        // 스프링에서 트랜잭션 기본 전파 옵션은 REQUIRES, 즉 부모 트랜잭션이 있으면 트랜잭션을 만들지 않는다.
        // Test 클래스에 트랜잭션이 걸려있으므로 서비스 계층의 자식 트랜잭션은 실행되지 않게 된다.
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }


    @DisplayName("게시판과 OneToMany 관계인 Member에서 N + 1 문제가 발생할 수 있다.")
    @Test
    void n_test() {
        saveManyBoardsAndMembers();

        em.clear();
        List<Member> members = memberRepository.findAllByJPQL();
        // 만약 n + 1 문제가 해결되지 못한다면 해당 코드에 select가 여러번 진행
        members.forEach(tester -> tester.getBoards().size());

        assertThat(members.size()).isEqualTo(11);
    }

    private void saveManyBoardsAndMembers() {
        for (int i = 0; i < 10; i++) {
            Member memberA = Member.builder()
                .loginId("testerAA" + i)
                .password("1111111" + i)
                .nickname("tester" + i)
                .build();
            memberRepository.save(memberA);
            BoardRequest request = BoardRequest.builder()
                .title("title" + i)
                .content("content" + i)
                .build();
            boardService.register(memberA, request);
        }
    }

    @DisplayName("게시판에 락을 걸어 여러 쓰레드가 동시에 접근해도 조회수가 제대로 증가한다.")
    @Test
    void lockTest() throws InterruptedException {
        int numberOfThread = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThread);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
        BoardRequest request = new BoardRequest("테스트", "hello");
        transaction.execute((status -> memberRepository.save(member)));
        Board board = transaction.execute((status) -> boardService.register(member, request));

        for (int i = 0; i < numberOfThread; i++) {
            executorService.execute(() -> {
                try {
                     transaction.execute((status) -> boardService.getBoard(board.getId()));
                } catch (Exception e) {
                    System.out.println("e.getClass() = " + e.getClass());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Board board1 = boardService.getBoard(board.getId());
        assertThat(board1.getViews()).isEqualTo(numberOfThread + 1);
    }
}
