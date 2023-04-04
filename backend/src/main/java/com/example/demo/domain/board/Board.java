package com.example.demo.domain.board;

import com.example.demo.domain.BaseTimeEntity;
import com.example.demo.domain.member.Member;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity(name = "BOARD")
@AllArgsConstructor
@NoArgsConstructor
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private Long views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void setMember(Member member) {
        this.member = member;
        if (member != null) {
            member.addBoard(this);
        }
    }

    public void update(Board updateBoard) {
        if (updateBoard.title != null) {
            this.title = updateBoard.title;
        }

        if (updateBoard.content != null) {
            this.content = updateBoard.content;
        }
    }

    public void increaseViews() {
        views++;
    }

    @PrePersist
    private void initBoardInPersist() {
        if (views == null) {
            views = 0L;
        }
    }
}

