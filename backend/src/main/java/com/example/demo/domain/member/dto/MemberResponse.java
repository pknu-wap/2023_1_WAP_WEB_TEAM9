package com.example.demo.domain.member.dto;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.Role;
import com.example.demo.domain.member.State;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {

    private String loginId;
    private String nickname;
    private String email;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
            .loginId(member.getLoginId())
            .nickname(member.getNickname())
            .email(member.getEmail())
            .createAt(member.getCreateAt())
            .updateAt(member.getUpdateAt())
            .deleteAt(member.getDeleteAt())
            .build();
    }
}
