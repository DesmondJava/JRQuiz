package com.jrquiz.service.impl;

import com.jrquiz.entity.Answer;
import com.jrquiz.entity.Question;
import com.jrquiz.entity.enums.QuestionType;
import com.jrquiz.repository.QuestionRepository;
import com.jrquiz.service.AnswerService;
import com.jrquiz.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Resource
    protected AnswerService answerService;
    @Autowired
    private QuestionRepository questionRepository;

    @Override
    @Caching(put = {
            @CachePut(value = "questions", key = "#question.id"),
            @CachePut(value = "enabledQuestions", key = "#question.id", condition = "#question.isActive")
    })
    public Question addQuestion(Question question) {
        return questionRepository.saveAndFlush(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public List<Question> getAllEnabledQuestions() {
        return questionRepository.findByActiveTrue();
    }

    @Override
    @Caching(put = {
            @CachePut(value = "questions", key = "#question.id"),
            @CachePut(value = "enabledQuestions", key = "#question.id", condition = "#question.isActive()")},
            evict = {@CacheEvict(value = "enabledQuestions", key = "#question.id", condition = "#!question.isActive()")}
    //TODO как следует обновить кэш questionAnswers enabled?
    )
    public Question updateQuestion(Question question) {
        return questionRepository.saveAndFlush(question);
    }

    @Override
    @Cacheable(value = "questions")
    public Question getQuestion(Long id) {
        return questionRepository.findOne(id);
    }

    @Override
    @Cacheable(value = "questionAnswers")
    public Set<Answer> getAnswers(Long questionId) {
        return questionRepository.findOne(questionId).getAnswers();
    }

    @Override
    @Caching (evict = {
        @CacheEvict(value = "questions"),
        @CacheEvict(value = "enabledQuestions", allEntries = true, condition = "#getQuestion(id).isActive()"),
        @CacheEvict(value = "questionAnswers")}
    )
    public void removeQuestion(Long id) {
        try {
            Question question = getQuestion(id);
            question.setTags(null);
            questionRepository.saveAndFlush(question);
            questionRepository.delete(id);
        } catch (Exception e) {
            //todo а что если вопрос используется в тесте?
            //Тут будет експешн
            e.printStackTrace();
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "enabledQuestions", allEntries = true, condition = "#getQuestion(id).isActive()")})
    public void disableQuestion(Long id) {
        Question question = getQuestion(id);
        question.setActive(false);
        updateQuestion(question);
    }

    @Override
    @Transactional
    @Caching(evict = {@CacheEvict(value = "questionAnswers", key = "#question.id")})
    public Question addAnswer(Answer answer, Question question) {
        if (question.getAnswers() == null)
            question.setAnswers(new HashSet<>());

        if (question.getQuestionType().equals(QuestionType.TYPEANSWER)) {
            for (Answer ans : question.getAnswers())
                removeAnswer(ans);
            answer.setIsCorrect(true);
        }

        answer.setQuestion(question);
        question.getAnswers().add(answer);
        return questionRepository.saveAndFlush(question);
    }

    @Override
    @Transactional
    @CacheEvict(value = "questionAnswers", key = "#answer.question.id")
    public void removeAnswer(Answer answer) {
        Question question = answer.getQuestion();
        if (question.getAnswers() != null) {
            if (question.getAnswers().contains(answer)) {
                Iterator<Answer> iter = question.getAnswers().iterator();
                while (iter.hasNext()) {
                    Answer currentAnswer = iter.next();
                    if (answer.equals(currentAnswer)) {
                        answerService.removeAnswer(currentAnswer.getId());
                        iter.remove();
                        updateQuestion(question);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public Page<Question> getAllPublishedQuestionsByCreatedDateAsc(Pageable pageable) {
        Page<Question> questions = questionRepository.findByOrderByCreateDateAsc(pageable);
        return questions;
    }

    @Override
    public Page<Question> getAllPublishedQuestionsByCreatedDateDesc(Pageable pageable) {
        Page<Question> questions = questionRepository.findByOrderByCreateDateDesc(pageable);
        return questions;
    }

    @Override
    public Page<Question> getAllQuestionsByIdAsc(Pageable pageable) {
        Page<Question> questions = questionRepository.findByOrderByIdAsc(pageable);;
        return questions;
    }

    @Override
    public Page<Question> getAllQuestionsByIdDesc(Pageable pageable) {
        Page<Question> questions = questionRepository.findByOrderByIdDesc(pageable);;
        return questions;
    }

    @Override
    public Page<Question> getAllQuestionsByTitleAsc(Pageable pageable) {
        Page<Question> questions = questionRepository.findByOrderByTitleAsc(pageable);
        return questions;
    }

    @Override
    public Page<Question> getAllQuestionsByTitleDesc(Pageable pageable) {
        Page<Question> questions = questionRepository.findByOrderByTitleDesc(pageable);
        return questions;
    }

    @Override
    public Page<Question> getAllQuestionsByComplexityAsc(Pageable pageable) {
        Page<Question> questions = questionRepository.findByOrderByComplexityAsc(pageable);
        return questions;
    }

    @Override
    public Page<Question> getAllQuestionsByComplexityDesc(Pageable pageable) {
        Page<Question> questions = questionRepository.findByOrderByComplexityDesc(pageable);
        return questions;
    }

    @Override
    public Page<Question> getAllQuestionsByCreatedByAsc(Pageable pageable) {
        Page<Question> questions = questionRepository.findByOrderByCreatedByAsc(pageable);
        return questions;
    }

    @Override
    public Page<Question> getAllQuestionsByCreatedByDesc(Pageable pageable) {
        Page<Question> questions = questionRepository.findByOrderByCreatedByDesc(pageable);
        return questions;
    }

    @Override
    public List<Question> findQuestions(String title) {
        return questionRepository.findByTitleLike("%" + title + "%");
    }

    @Override
    public Long getAmountOfQuestions() {
        return questionRepository.count();
    }

    @Override
    public Long getActiveTrue() {
        return questionRepository.countByActiveTrue();
    }
}
