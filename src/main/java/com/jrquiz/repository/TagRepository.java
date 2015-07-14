package com.jrquiz.repository;

import com.jrquiz.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByTagName(String tagName);
    List<Tag> findByOrderByIdDesc();
}
