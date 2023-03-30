package com.example.demo.repository;

import com.example.demo.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByLoginId(String loginId);

    @Query("select distinct m from Member m left outer join fetch m.boards")
    List<Member> findAllByJPQL();
}
