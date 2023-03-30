package com.example.demo.repository;

import com.example.demo.domain.board.Board;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Board> findBoardById(Long id);
}
