package com.jrquiz.service.impl;

import com.jrquiz.entity.Answer;
import com.jrquiz.repository.AnswerRepository;
import com.jrquiz.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    @CachePut(value = "answers", key = "#answer.id")
    public Answer addAnswer(Answer answer) {
        return answerRepository.saveAndFlush(answer);
    }

    @Override
    @Cacheable(value = "answers")
    public Answer getAnswer(Long id) {
        return answerRepository.findOne(id);
    }

    @Override
    @CachePut(value = "answers", key = "#answer.id")
    public Answer updateAnswer(Answer answer) {
        return answerRepository.saveAndFlush(answer);
    }

    @Override
    @CacheEvict(value = "answers")
    public void removeAnswer(Long id) {
        answerRepository.delete(id);
    }
}
