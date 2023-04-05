package com.example.demo.repository;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.tag.BoardTag;
import com.example.demo.domain.tag.Tag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardTagRepository extends JpaRepository<BoardTag, Long> {

    @Query("select b.board from BoardTag b where b.tag.name = :tagName")
    List<Board> findBoardByTagName(@Param("tagName") String tagName);

    @Query("select b.tag from BoardTag b group by b.tag order by count(b.tag) desc")
    List<Tag> findTagByCount(Pageable pageable);
}
