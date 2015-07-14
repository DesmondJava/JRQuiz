package com.jrquiz.service.impl;

import com.jrquiz.entity.Question;
import com.jrquiz.entity.Tag;
import com.jrquiz.entity.User;
import com.jrquiz.entity.statistics.UserAnswer;
import com.jrquiz.repository.QuestionRepository;
import com.jrquiz.service.QuizService;
import com.jrquiz.service.StatisticService;
import com.jrquiz.service.UserAnswerService;
import com.jrquiz.service.UserService;
import com.jrquiz.support.stat.TagStatistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public int passedQuizzes(User user) {
        return  quizService.countFinishedTests(user);
    }

    @Override
    public int getAmountQuizzes() {
        return quizService.getAllQuizzes().size();
    }

    @Override
    public int answeredQuestions(User user) {
        List<UserAnswer> answers = userAnswerService.findByUser(user);
        Set<Question> questions = new HashSet<>();
        for(UserAnswer answ : answers){
            questions.add(answ.getQuestion());
        }
        return questions.size();
    }

    @Override
    public int correctAnsweredQuestions(User user) {
        return userAnswerService.countOfCorrectAnsweredQuestions(user);
    }

    @Override
    public Tag getPopularTag(User user) {
        return null;
    }

    @Override
    public List<TagStatistic> getTagStatistic(User user) {
        return userAnswerService.passingTagStatistic(user);
    }


    @Override
    public int getPointsGained(User user) {
        List<UserAnswer> answers = userAnswerService.findByUser(user);
        Set<Question> questions = new HashSet<>();
        int pointsGained = 0;
        for(UserAnswer answ : answers) {
            if(answ.getAnswer().getIsCorrect()){
                questions.add(answ.getQuestion());
            }
        }
        for(Question question : questions) {
            pointsGained += question.getComplexity();
        }
        return pointsGained;
    }

    @Override
    public int getAmountPoints() {
        int amountPoints = 0;
        for(Question question : questionRepository.findAll()){
            if(question.getComplexity() != null) {
                amountPoints += question.getComplexity();
            }
        }
        return amountPoints;
    }


    @Override
    public int getAmountQuestions() {
        List<Question> questionList = questionRepository.findAll();
        if(questionList != null) {
            return questionList.size();
        }

        return 0;
    }
}
