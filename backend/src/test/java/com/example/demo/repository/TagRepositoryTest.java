package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.tag.BoardTag;
import com.example.demo.domain.tag.Tag;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private BoardTagRepository boardTagRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("이름이 같은 태그가 존재하면 True를 반환한다.")
    @Test
    void existsByName() {
        Tag tag = Tag.builder()
            .name("인생")
            .build();
        tagRepository.save(tag);

        assertThat(tagRepository.existsByName(tag.getName())).isTrue();
    }

    @DisplayName("태그 이름이 포함된 Board들이 반환")
    @Test
    void findAllByTagName() {
        Tag tag1 = Tag.builder()
            .name("인생")
            .build();
        Tag tag2 = Tag.builder()
            .name("돈")
            .build();
        List<Tag> tags = List.of(tag1, tag2);
        tagRepository.saveAll(tags);

        saveBoardTagsForTest(tags);

        em.clear();

        List<Board> boardsByTag1 = boardTagRepository.findBoardByTagName(tag1.getName());
        List<Board> boardsByTag2 = boardTagRepository.findBoardByTagName(tag2.getName());

        assertThat(boardsByTag1.size()).isEqualTo(100);
        assertThat(boardsByTag2.size()).isEqualTo(50);
    }

    @DisplayName("많이 게시판이 가지는 태그를 조회")
    @Test
    void findTagByCount() {
        Tag tag1 = Tag.builder()
            .name("인생")
            .build();
        Tag tag2 = Tag.builder()
            .name("돈")
            .build();
        Tag tag3 = Tag.builder()
            .name("진로")
            .build();
        List<Tag> tags = List.of(tag1, tag2, tag3);
        tagRepository.saveAll(tags);
        saveBoardTagsForTest(tags);

        List<Tag> tagsByCount = boardTagRepository.findTagByCount(Pageable.ofSize(2));

        tagsByCount.forEach(tag -> System.out.println("tag.getName() = " + tag.getName()));
        assertThat(tagsByCount).containsExactly(tag1, tag2);
    }

    private void saveBoardTagsForTest(List<Tag> tags) {
        for (int i = 1; i <= 100; i++) {
            saveBoardTagsWithIndexDivision(tags, i);
        }
    }

    private void saveBoardTagsWithIndexDivision(List<Tag> tags, int index) {
        for (int i = 0; i < tags.size(); i++) {
            if (isDivisible(index, i + 1)) {
                Tag tag = tags.get(i);
                Board board = Board.builder()
                    .title("board" + i)
                    .content("this is board" + i)
                    .build();
                BoardTag boardTag = BoardTag.associate(board, tag);
                boardTagRepository.save(boardTag);
            }
        }
    }

    private boolean isDivisible(int dividend, int divisor) {
        return dividend % divisor == 0;
    }
}
