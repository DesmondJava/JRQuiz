package com.jrquiz.service;

import com.jrquiz.entity.Question;
import com.jrquiz.entity.Tag;

import java.util.List;
import java.util.Set;

public interface TagService {

    Tag addTag(Tag tag);

    List<Tag> getAllTags();

    Set<Question> getAllQuestionsByTags(Set<Tag> tags);

    Tag getTag(Long tagId);

    Tag getTagByName(String tagName);

    void removeTag(Long tagId);

    Tag updateTag(Tag tag);
}
