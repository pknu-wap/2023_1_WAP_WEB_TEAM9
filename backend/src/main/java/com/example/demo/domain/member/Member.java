package com.example.demo.domain.member;

import com.example.demo.domain.board.Board;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Length(min = 8, max = 20)
    private String loginId;

    @NotBlank
    @Length(min = 8, max = 20)
    private String password;

    @NotNull
    @Size(max = 20)
    private String nickname;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", orphanRemoval = true)
    @Builder.Default
    private List<Board> boards = new ArrayList<>();

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    @CreatedDate
    private LocalDateTime createAt;

    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime updateAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime deleteAt;

    public void addBoard(Board board) {
        boards.add(board);
    }

    public void deleteBoard(Board board) {
        board.setMember(null);
        if (!boards.isEmpty()) {
            boards.remove(board);
        }
    }
}
