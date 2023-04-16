package com.example.demo.repository;

import com.example.demo.domain.tag.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByName(String name);

    List<Tag> findAllByNameIn(List<String> names);
}
