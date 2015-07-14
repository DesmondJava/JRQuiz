package com.jrquiz.service;

import com.jrquiz.entity.Question;
import com.jrquiz.entity.Quiz;
import com.jrquiz.entity.User;
import com.jrquiz.entity.statistics.UserAnswer;
import com.jrquiz.support.stat.TagStatistic;


import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

public interface UserAnswerService {
    List<UserAnswer> findByUser(User user);
    List<UserAnswer> findByUserAndQuiz(User user, Quiz quiz);
    // useful method for getting next incomplete question
    List<UserAnswer> findByUserAndQuizAndAnswerIsNullOrderByQuestionIndex(User user, Quiz quiz);
    // useful method for getting incomplete quizzes
    List<UserAnswer> findByUserAndAnswerIsNullOrderByQuestionIndex(User user);
    List<UserAnswer> findByUserAndQuestionAndQuiz(User user, Question question, Quiz quiz);
    @Transactional
    List<UserAnswer> findByUserAndAnswerIsNullDistinctQuiz(User user);
    List<UserAnswer> findByQuestion(Question question);
    Set<Question> getAllAnsweredQuestion(User user);

    UserAnswer getUserAnswer(Long id);
    UserAnswer addUserAnswer(UserAnswer userAnswer);
    UserAnswer updateUserAnswer(UserAnswer userAnswer);
    void removeUserAnswer(Long id);
    void removeUserAnswer(UserAnswer userAnswer);
    void removeUserAnswerForCurrentQuiz(Quiz quiz);
    int countOfAnsweredQuestions(User user);
    int countOfAnsweredQuestionsByQuiz(User user, Quiz quiz);
    int countOfCorrectAnsweredQuestions(User user);
    List<TagStatistic> passingTagStatistic(User user);
}
