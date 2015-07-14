package com.jrquiz.service;

import com.jrquiz.entity.Question;
import com.jrquiz.entity.Tag;
import com.jrquiz.entity.Quiz;
import com.jrquiz.entity.User;
import com.jrquiz.exceptions.AccessDeniedException;
import com.jrquiz.exceptions.NoSuitableQuestionsException;
import com.jrquiz.exceptions.RemoveQuizException;
import com.jrquiz.support.quiz.AnswersForQuiz;
import com.jrquiz.support.quiz.QuestionsForQuiz;

import java.util.List;
import java.util.Set;

public interface QuizService {
    Quiz getQuiz(Long quizID);
    Set<Question> getQuestions(Long quizID);

    Quiz addQuiz(Quiz quiz);

    Set<Quiz> findQuizzesByTag(Tag tag);
    Set<Quiz> findQuizzesByUser(User user);
    Set<Quiz> findUncompletedQuizzesByUser(User user);

    List<Quiz> getAllQuizzes();

    Quiz updateQuiz(Quiz quiz);

    void removeQuiz(Long quizID) throws RemoveQuizException;
    void removeQuiz(Quiz entity) throws RemoveQuizException;

    int countFinishedTests(User user);

    Quiz createQuiz(User user, Set<Tag> tags, int quantity, byte minComplexity, byte maxComplexity) throws NoSuitableQuestionsException;
    void submitAnswer(QuestionsForQuiz questionsForQuiz, Quiz quiz, User user ,Long... answerIds);
    List<QuestionsForQuiz>initializeQuiz(Quiz quiz, User user) throws AccessDeniedException;
    AnswersForQuiz prepareAnswersForQuiz(QuestionsForQuiz questionsForQuiz, User user, Quiz quiz);
}
