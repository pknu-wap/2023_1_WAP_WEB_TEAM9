package com.example.demo.service;

import com.example.demo.domain.tag.Tag;
import com.example.demo.domain.tag.dto.TagRequest;
import com.example.demo.repository.BoardTagRepository;
import com.example.demo.repository.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    private final BoardTagRepository boardTagRepository;

    @Transactional
    public Tag saveTag(TagRequest request) {
        Tag tag = request.toTag();
        if (!tagRepository.existsByName(tag.getName())) {
            return tagRepository.save(tag);
        } else {
            throw new IllegalArgumentException("이미 존재하는 태그입니다.");
        }
    }

    public List<Tag> findPopularTag(Integer size) {
        return boardTagRepository.findTagByCount(Pageable.ofSize(size));
    }

    public List<Tag> findAllByNameIn(List<String> tagNames) {
        return tagRepository.findAllByNameIn(tagNames);
    }
}
