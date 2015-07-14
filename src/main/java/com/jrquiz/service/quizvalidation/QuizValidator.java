package com.jrquiz.service.quizvalidation;

import com.jrquiz.entity.Answer;
import com.jrquiz.entity.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class QuizValidator {
    @Autowired
    protected QuestionValidator questionValidator;

    public QuizValidationResult validate(Map<Question, Answer[]> userAnswers) throws Exception {
        QuizValidationResult result = new QuizValidationResult();

        Integer totalPercentage = 0;
        for (Question question : userAnswers.keySet())
        {
            QuestionValidationResult questionValidationResult = questionValidator.validateQuestion(question, userAnswers.get(question));

            totalPercentage += questionValidationResult.getPercentage();
        }
        result.setTotalPercentage(totalPercentage / userAnswers.size());

        return result;
    }

}
