package com.jrquiz.service.impl;

import com.jrquiz.entity.Question;
import com.jrquiz.entity.Tag;
import com.jrquiz.repository.TagRepository;
import com.jrquiz.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    @Caching(put = {
            @CachePut(value = "tags", key = "#tag.id"),
            @CachePut(value = "tags", key = "#tag.tagname")
    })
    public Tag addTag(Tag tag) {

        try {
            return tagRepository.saveAndFlush(tag);
        } catch (Exception e) {
        }
        return getTagByName(tag.getTagName());
        //TODO Придумать как это лучше реализовать
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findByOrderByIdDesc();
    }

    @Override
    public Set<Question> getAllQuestionsByTags(Set<Tag> tags) {
        HashSet<Question> questions = new HashSet<>();
        for (Tag tag : tags) {
            questions.addAll(tag.getQuestions());
        }
        return questions;
    }

    @Override
    @Cacheable(value = "tags")
    public Tag getTag(Long tagId) {
        return tagRepository.findOne(tagId);
    }

    @Override
    @Cacheable(value = "tags")
    public Tag getTagByName(String tagName) {
        return tagRepository.findByTagName(tagName);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "tags", key = "#id"),
            @CacheEvict(value = "tags", key = "#getTag(id).tagname")
    })
    public void removeTag(Long tagId) {
        Tag tag = tagRepository.findOne(tagId);
        if (tag.getQuestions().isEmpty())
            tagRepository.delete(tagId);
        else
            throw new RuntimeException("Tags have associated questions and can not be removed");//TODO исключение временное, потом поменять
    }

    @Override
    @Caching(put = {
            @CachePut(value = "tags", key = "#tag.id"),
            @CachePut(value = "tags", key = "#tag.tagname")
    })
    public Tag updateTag(Tag tag) {
        return tagRepository.saveAndFlush(tag);
    }


}
