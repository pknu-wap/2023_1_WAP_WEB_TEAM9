package com.example.demo.domain.tag;

import com.example.demo.domain.BaseTimeEntity;
import com.example.demo.domain.board.Board;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BoardTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "TAG_ID")
    private Tag tag;

    public static BoardTag associate(Board board, Tag tag) {
        BoardTag boardTag = new BoardTag();
        boardTag.setBoard(board);
        boardTag.setTag(tag);
        return boardTag;
    }

    public void setBoard(Board board) {
        this.board = board;
        this.board.addBoardTags(this);
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
