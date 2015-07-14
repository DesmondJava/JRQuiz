package com.jrquiz.service.quizvalidation;

import java.util.ArrayList;

public class QuizValidationResult extends ArrayList<QuestionValidationResult> {
    private Integer totalPercentage;

    public Integer getTotalPercentage() {
        return totalPercentage;
    }

    void setTotalPercentage(Integer totalPercentage) {
        this.totalPercentage = totalPercentage;
    }
}
