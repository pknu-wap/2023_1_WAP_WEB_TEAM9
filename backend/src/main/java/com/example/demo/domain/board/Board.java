package com.example.demo.domain.board;

import com.example.demo.domain.BaseTimeEntity;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.tag.BoardTag;
import com.example.demo.domain.tag.Tag;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
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

@Getter
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "board", orphanRemoval = true)
    @Builder.Default
    private Set<BoardTag> boardTags = new HashSet<>();

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

    public void updateTags(List<Tag> tags) {
        if (tags != null) {
            this.boardTags = tags.stream()
                .map(tag -> BoardTag.associate(this, tag)).collect(Collectors.toSet());
        }
    }

    public void increaseViews() {
        views++;
    }

    public void deleteBoardTag(BoardTag boardTag) {
        this.boardTags.remove(boardTag);
    }

    public void addBoardTag(BoardTag boardTag) {
        this.boardTags.add(boardTag);
    }

    public void addTags(Collection<Tag> tags) {
        this.boardTags.addAll(tags.stream()
            .map(tag -> BoardTag.associate(this, tag)).collect(Collectors.toSet()));
    }

    public void setViews(Long views) {
        this.views = views;
    }

    @PrePersist
    private void initBoardInPersist() {
        if (views == null) {
            views = 0L;
        }
    }
}

