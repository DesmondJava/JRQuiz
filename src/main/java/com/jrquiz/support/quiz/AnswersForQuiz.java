package com.jrquiz.support.quiz;

import com.jrquiz.entity.Answer;
import com.jrquiz.entity.enums.QuestionType;

import java.util.Set;

public class AnswersForQuiz {
    QuestionType questionType;
    String inputAnswerValue;
    Long[] checkBoxValues;
    Long radioAnswerValue;
    Set<Answer> answers;

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public Long getRadioAnswerValue() {
        return radioAnswerValue;
    }

    public void setRadioAnswerValue(Long radioAnswerValue) {
        this.radioAnswerValue = radioAnswerValue;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getInputAnswerValue() {
        return inputAnswerValue;
    }

    public void setInputAnswerValue(String inputAnswerValue) {
        this.inputAnswerValue = inputAnswerValue;
    }

    public Long[] getCheckBoxValues() {
        return checkBoxValues;
    }

    public void setCheckBoxValues(Long[] checkBoxValues) {
        this.checkBoxValues = checkBoxValues;
    }
}
