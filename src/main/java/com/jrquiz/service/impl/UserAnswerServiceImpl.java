package com.jrquiz.service.impl;

import com.jrquiz.entity.Question;
import com.jrquiz.entity.Quiz;
import com.jrquiz.entity.User;
import com.jrquiz.entity.statistics.UserAnswer;
import com.jrquiz.repository.UserAnswerRepository;
import com.jrquiz.service.UserAnswerService;
import com.jrquiz.support.stat.TagStatistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserAnswerServiceImpl implements UserAnswerService {
    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Override
    public List<UserAnswer> findByUser(User user) {
        return userAnswerRepository.findByUser(user);
    }

    @Override
    public List<UserAnswer> findByUserAndQuiz(User user, Quiz quiz) {
        return userAnswerRepository.findByUserAndQuiz(user, quiz);
    }

    @Override
    public List<UserAnswer> findByUserAndQuizAndAnswerIsNullOrderByQuestionIndex(User user, Quiz quiz) {
        return userAnswerRepository.findByUserAndQuizAndAnswerIsNullOrderByQuestionIndex(user, quiz);
    }

    @Override
    public List<UserAnswer> findByUserAndAnswerIsNullOrderByQuestionIndex(User user) {
        return userAnswerRepository.findByUserAndAnswerIsNullOrderByQuestionIndex(user);
    }

    @Override
    public List<UserAnswer> findByUserAndQuestionAndQuiz(User user, Question question, Quiz quiz) {
        return userAnswerRepository.findByUserAndQuestionAndQuiz(user, question, quiz);
    }

    @Transactional
    @Override
    public List<UserAnswer> findByUserAndAnswerIsNullDistinctQuiz(User user) {
        return userAnswerRepository.findByUserAndAnswerIsNullDistinctQuiz(user);
    }

    @Override
    public List<UserAnswer> findByQuestion(Question question) {
        return userAnswerRepository.findByQuestion(question);
    }

    @Override
    @Cacheable(value = "userAnswers")
    public UserAnswer getUserAnswer(Long id) {
        return userAnswerRepository.findOne(id);
    }

    @Override
    @Caching(put = {@CachePut(value = "userAnswers", key = "#userAnswer.id")})
    public UserAnswer addUserAnswer(UserAnswer userAnswer) {
        return userAnswerRepository.saveAndFlush(userAnswer);
    }

    @Override
    @Caching(put = {@CachePut(value = "userAnswers", key = "#userAnswer.id")})
    public UserAnswer updateUserAnswer(UserAnswer userAnswer) {
        return userAnswerRepository.saveAndFlush(userAnswer);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "userAnswers", key = "#id")})
    public void removeUserAnswer(Long id) {
        userAnswerRepository.delete(id);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "userAnswers", key = "#userAnswer.id")})
    public void removeUserAnswer(UserAnswer userAnswer) {
        userAnswerRepository.delete(userAnswer);
    }

    @Override
    public void removeUserAnswerForCurrentQuiz(Quiz quiz) {
        List<UserAnswer> userAnswersForRemove = userAnswerRepository.findByQuiz(quiz);
        for (UserAnswer userAnswer : userAnswersForRemove) {
            removeUserAnswer(userAnswer);
        }
    }

    private List<UserAnswer> findByUserAndScore(User user, int score) {
        return userAnswerRepository.findByUserAndScore(user, score);
    }

    @Override
    public Set<Question> getAllAnsweredQuestion(User user) {
        List<UserAnswer> userAnswerList = findByUserAndScore(user, 100);
        Set<Question> questionSet = new HashSet<>();

        for (UserAnswer userAnswer : userAnswerList) {
            questionSet.add(userAnswer.getQuestion());
        }
        return questionSet;
    }

    @Override
    public int countOfAnsweredQuestions(User user) {
        if (userAnswerRepository.countOfAnsweredQuestionsByUser(user) != null)
            return userAnswerRepository.countOfAnsweredQuestionsByUser(user);
        else
            return 0;
    }

    @Override
    public int countOfAnsweredQuestionsByQuiz(User user, Quiz quiz) {
        if (userAnswerRepository.countOfAnsweredQuestionsByUserAndQuiz(user, quiz) != null)
            return userAnswerRepository.countOfAnsweredQuestionsByUserAndQuiz(user, quiz);
        else
            return 0;
    }

    @Override
    public int countOfCorrectAnsweredQuestions(User user) {
        if (userAnswerRepository.countOfCorrectAnsweredQuestionsByUser(user) != null)
            return userAnswerRepository.countOfCorrectAnsweredQuestionsByUser(user);
        else
            return 0;
    }

    @Override
    public List<TagStatistic> passingTagStatistic(User user) {
        return userAnswerRepository.statisticOfPassingQuestionsByTag(user);
    }
}
