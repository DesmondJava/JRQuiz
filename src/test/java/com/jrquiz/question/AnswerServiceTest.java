package com.jrquiz.question;

import com.jrquiz.entity.Answer;
import com.jrquiz.entity.Question;
import com.jrquiz.entity.enums.QuestionType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNull;

public class AnswerServiceTest extends EntityAndServiceBaseTest {

    private String answerText = "NewAnswerForTest";
    Question newQuestion;

    @Before
    public void addQuestion() {
        Question question = new Question();
        question.setTitle("NewQuestionForAnswer");
        question.setText("NewQuestionForAnswer");
        question.setQuestionType(QuestionType.MULTIANSWER);
        newQuestion = questionService.addQuestion(question);
    }

    @After
    public void deleteQuestion() {
        questionService.removeQuestion(newQuestion.getId());
    }

    @Test
    public void addAnswer() {
        Answer answer = new Answer();
        answer.setAnswerText(answerText);
        answer.setIsCorrect(true);
        newQuestion = questionService.addAnswer(answer, newQuestion);
        answer = newQuestion.getAnswers().iterator().next();
        Long id = answer.getId();
        assertEquals(answerText, answerService.getAnswer(id).getAnswerText());
        questionService.removeAnswer(answer);
    }

    @Test
    public void removeAnswer() {
        Answer answer = new Answer();
        answer.setAnswerText(answerText);
        answer.setIsCorrect(true);
        newQuestion = questionService.addAnswer(answer, newQuestion);
        answer = newQuestion.getAnswers().iterator().next();
        Long id = answer.getId();
        questionService.removeAnswer(answer);
        Answer deletedAnswer = answerService.getAnswer(id);
        assertNull(deletedAnswer);
    }

}
