package com.example.demo.domain.board;

import com.example.demo.domain.comment.Comment;
import com.example.demo.domain.member.Member;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Builder
@Entity(name = "BOARD")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private Long views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board")
    private List<Comment> comments;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    @CreatedDate
    private LocalDateTime createAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    @LastModifiedDate
    private LocalDateTime updateAt;

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

        updateAt = LocalDateTime.now();
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

