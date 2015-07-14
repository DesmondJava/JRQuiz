package com.jrquiz.support.quiz;

import com.jrquiz.entity.Question;
import com.jrquiz.entity.statistics.UserAnswer;

import java.util.List;


public class QuestionsForQuiz {
    Question question;
    Boolean isAnswered;
    List<UserAnswer> answers;
    int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public QuestionsForQuiz() {
        this.isAnswered = false;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Boolean getIsAnswered() {
        return isAnswered;
    }

    public void setIsAnswered(Boolean isAnswered) {
        this.isAnswered = isAnswered;
    }

    public List<UserAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<UserAnswer> answers) {
        this.answers = answers;
    }
}
