package com.jrquiz.question;

import com.jrquiz.entity.Answer;
import com.jrquiz.entity.Question;
import com.jrquiz.entity.Quiz;
import com.jrquiz.entity.User;
import com.jrquiz.entity.enums.QuestionType;
import com.jrquiz.exceptions.RemoveQuizException;
import com.jrquiz.service.quizvalidation.QuestionValidator;
import com.jrquiz.service.quizvalidation.QuizValidator;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class QuizServiceTest extends EntityAndServiceBaseTest {

    private static final String ASSERTION_FAILED_MESSAGE_TEMPLATE = "Test %s failed. The Test gotten from DB differs from the Test saved into DB.";
    private static final String TEST_MODE = "test mode";
    private static final Timestamp TEST_TIMESTAMP = new Timestamp(System.currentTimeMillis());
    private Set<Question> testQuestions = new HashSet<>();
    private User testUser = null;

    @Resource
    private QuestionValidator questionValidator;

    @Resource
    private QuizValidator quizValidator;

    private Quiz generateTest()
    {
        Quiz generatedQuiz = new Quiz();
        generatedQuiz.setCreated(TEST_TIMESTAMP);
        generatedQuiz.setMode(TEST_MODE);
        generatedQuiz.setQuestions(testQuestions);
        generatedQuiz.setUser(testUser);

        return generatedQuiz;
    }

    @org.junit.Test
    @Transactional
    public void addAndGetTestTest()
    {
        Quiz generatedQuiz = generateTest();
        Quiz readQuiz = quizService.addQuiz(generatedQuiz);
        Long testID = readQuiz.getId();
        readQuiz = quizService.getQuiz(testID);
        assertEquals(String.format(ASSERTION_FAILED_MESSAGE_TEMPLATE, "adding/getting"), generatedQuiz, readQuiz);
        try {
            quizService.removeQuiz(testID);
        } catch (RemoveQuizException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    @Transactional
    public void updateTestTest()
    {
        em = emf.createEntityManager();

        Quiz generatedQuiz = generateTest();
        Quiz readQuiz = quizService.addQuiz(generatedQuiz);
        Long testID = readQuiz.getId();

        Quiz quizToUpdate = quizService.getQuiz(testID);
        Query userQuery = em.createQuery("SELECT u FROM User u ORDER BY u.id");
        List<User> users = userQuery.getResultList();
        assertTrue("No data in table User found", users.size() > 0);
        quizToUpdate.setUser(users.get(0));
        quizToUpdate.setMode(quizToUpdate.getMode() + ":updated");
        Set<Question> updatedQuestions = quizToUpdate.getQuestions();
        Query questionQuery = em.createQuery("SELECT q FROM Question q ORDER BY q.id");
        List<Question> questions = questionQuery.getResultList();
        assertTrue("No data in table Questions found", questions.size() > 0);
        updatedQuestions.add(questions.get(0));
        quizToUpdate.setQuestions(updatedQuestions);
        quizService.updateQuiz(quizToUpdate);
        Quiz updatedQuiz = quizService.getQuiz(testID);

        assertEquals(String.format(ASSERTION_FAILED_MESSAGE_TEMPLATE, "updating"), updatedQuiz, quizToUpdate);
        try {
            quizService.removeQuiz(testID);
        } catch (RemoveQuizException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    @Transactional
    public void removeTestTest() throws RemoveQuizException {
        Quiz generatedQuiz = generateTest();
        Quiz readQuiz = quizService.addQuiz(generatedQuiz);
        Long testID = readQuiz.getId();

        quizService.removeQuiz(readQuiz);
        Quiz deletedQuiz = quizService.getQuiz(testID);
        assertNull(deletedQuiz);
    }

    @org.junit.Test
    public void verificationQuiz() throws Exception {
        // testing validation service
        List<Quiz> quizs = quizService.getAllQuizzes();
        final int MAX_QUIZS = 5;
        int quizzesCounter = 0;
        final int MAX_QUESTIONS_PER_QUIZ = 10;

        for (Quiz quiz : quizs)
        {
            quizzesCounter++;
            if (quizzesCounter > MAX_QUIZS) break;
            Map<Question, Answer[]> emulatedCorrectUserAnswers = new HashMap<>();
            Map<Question, Answer[]> emulatedIncorrectUserAnswers = new HashMap<>();

            int questionsCounter = 0;
            for (Question question : quiz.getQuestions())
            {
                if (question.getAnswers().size() > 0)
                {
                    questionsCounter++;
                    if (questionsCounter > MAX_QUESTIONS_PER_QUIZ) break;
                    Answer[] correctAnswers = generateUserAnswers(question, true);
                    Answer[] incorrectAnswers = generateUserAnswers(question, false);
                    emulatedCorrectUserAnswers.put(question,correctAnswers );
                    emulatedIncorrectUserAnswers.put(question, incorrectAnswers);

                    assertEquals(100, (int) questionValidator.validateQuestion(question, correctAnswers)  .getPercentage());
                    assertEquals(0, (int) questionValidator.validateQuestion(question, incorrectAnswers).getPercentage());
                }
            }

            assertEquals(100, (int) quizValidator.validate(emulatedCorrectUserAnswers)  .getTotalPercentage());
            assertEquals(0,   (int) quizValidator.validate(emulatedIncorrectUserAnswers).getTotalPercentage());
        }
    }

    private Answer[] generateUserAnswers(Question question, Boolean allCorrectAnswers)
    {
        List<Answer> userAnswers = new ArrayList<>();
        if (question.getQuestionType() == QuestionType.MULTIANSWER) {
            for (Answer answer : question.getAnswers()) {
                if (answer.getIsCorrect() == allCorrectAnswers)
                {
                    userAnswers.add(answer);
                }
            }
        } else if (question.getQuestionType() == QuestionType.SINGLEANSWER) {
            for (Answer answer : question.getAnswers()) {
                if (answer.getIsCorrect() == allCorrectAnswers)
                {
                    userAnswers.add(answer);
                    break; // only 1 answer allowed
                }
            }
        } else if (question.getQuestionType() == QuestionType.TYPEANSWER) {
            if (question.getAnswers().size() > 0)
            {
                userAnswers.addAll(question.getAnswers());
                if (!allCorrectAnswers)
                {
                    for (Answer userAnswer : userAnswers)
                    {
                        userAnswer.setAnswerText("bla bla " + userAnswer.getAnswerText());
                    }
                }
            }
            else
            {
                Answer userAnswer = new Answer();
                userAnswer.setQuestion(question);
                userAnswer.setAnswerText("bla bla");
                userAnswers.add(userAnswer);
            }
        }
        return userAnswers.toArray(new Answer[userAnswers.size()]);
    }
}
