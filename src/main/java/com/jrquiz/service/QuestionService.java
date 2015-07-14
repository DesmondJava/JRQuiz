package com.jrquiz.service;


import com.jrquiz.entity.Answer;
import com.jrquiz.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface QuestionService {
    Question addQuestion(Question question);

    List<Question> getAllQuestions();

    List<Question>  getAllEnabledQuestions();

    Question updateQuestion(Question question);

    Question getQuestion(Long questionId);

    Set<Answer> getAnswers(Long questionId);

    void removeQuestion(Long id);

    Question addAnswer(Answer answer, Question question);

    void removeAnswer(Answer answer);

    void disableQuestion(Long id);

    Page<Question> getAllPublishedQuestionsByCreatedDateAsc(Pageable pageable);
    Page<Question> getAllPublishedQuestionsByCreatedDateDesc(Pageable pageable);

    Page<Question> getAllQuestionsByIdAsc(Pageable pageable);
    Page<Question> getAllQuestionsByIdDesc(Pageable pageable);

    Page<Question> getAllQuestionsByTitleAsc(Pageable pageable);
    Page<Question> getAllQuestionsByTitleDesc(Pageable pageable);

    Page<Question> getAllQuestionsByComplexityAsc(Pageable pageable);
    Page<Question> getAllQuestionsByComplexityDesc(Pageable pageable);

    Page<Question> getAllQuestionsByCreatedByAsc(Pageable pageable);
    Page<Question> getAllQuestionsByCreatedByDesc(Pageable pageable);

    List<Question>  findQuestions(String title);

    Long getAmountOfQuestions();

    Long getActiveTrue();
}
