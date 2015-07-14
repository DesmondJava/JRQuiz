package com.jrquiz.controller;

import com.jrquiz.entity.Quiz;
import com.jrquiz.entity.User;
import com.jrquiz.entity.statistics.UserAnswer;
import com.jrquiz.exceptions.AccessDeniedException;
import com.jrquiz.service.QuizService;
import com.jrquiz.service.UserAnswerService;
import com.jrquiz.service.UserService;
import com.jrquiz.support.quiz.AnswersForQuiz;
import com.jrquiz.support.quiz.QuestionsForQuiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping(value = "/runquiz")
public class QuizRunController extends TemplateController {


    private QuizService quizService;
    private UserAnswerService userAnswerService;
    private UserService userService;

    @Autowired
    public QuizRunController(QuizService quizService, UserAnswerService userAnswerService, UserService userService) {
        this.quizService = quizService;
        this.userAnswerService = userAnswerService;
        this.userService = userService;
    }

    private Quiz quiz;

    private List<QuestionsForQuiz> questionsForCurrentQuiz;

    private Long currentQuestionIndex;

    private User user;


    @RequestMapping(value = "/{quizId}", method = RequestMethod.GET)
    public String showQuiz(@PathVariable Long quizId) {
        return "redirect:./" + quizId + "/" + 1;
    }

    @RequestMapping(value = "/{quizId}/{qId}", method = RequestMethod.GET)
    public String showQuizAndQuestion(@PathVariable Long quizId, @PathVariable Long qId, ModelMap model, Principal principal) throws AccessDeniedException {
        addHeaderAttributes(model);

        user = userService.getUser(principal);

        if (quiz == null || !quiz.getId().equals(quizId)|| questionsForCurrentQuiz == null) {
            quiz = quizService.getQuiz(quizId);
            questionsForCurrentQuiz = quizService.initializeQuiz(quiz, user);
        }

        currentQuestionIndex = getCurrentQuestionId(qId);

        AnswersForQuiz answerForQuiz = quizService.prepareAnswersForQuiz(getCurrentQuestionForQuiz(currentQuestionIndex), user, quiz);

        model.addAttribute("questionsInQuiz", questionsForCurrentQuiz);
        model.addAttribute("answerForQuiz", answerForQuiz);
        model.addAttribute("currentQuestion", getCurrentQuestionForQuiz(currentQuestionIndex));
        model.addAttribute("currentQuestionId", currentQuestionIndex);
        model.addAttribute("tid", quiz.getId());

        return "quiz/quiz";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitAnswer(@ModelAttribute AnswersForQuiz answerForQuiz) {

        if (answerForQuiz != null) {
            if (answerForQuiz.getRadioAnswerValue() != null) {
                deleteExistAnswersToQuestionInDb();
                Long answerId = answerForQuiz.getRadioAnswerValue();
                quizService.submitAnswer(getCurrentQuestionForQuiz(currentQuestionIndex), quiz, user, answerId);
                getCurrentQuestionForQuiz(currentQuestionIndex).setIsAnswered(true);

            } else if (answerForQuiz.getCheckBoxValues() != null) {
                deleteExistAnswersToQuestionInDb();
                quizService.submitAnswer(getCurrentQuestionForQuiz(currentQuestionIndex), quiz, user, answerForQuiz.getCheckBoxValues());
                getCurrentQuestionForQuiz(currentQuestionIndex).setIsAnswered(true);
            } else if (answerForQuiz.getInputAnswerValue() != null) {
                //TODO тут будет работа с введенным ответом
                deleteExistAnswersToQuestionInDb();
                getCurrentQuestionForQuiz(currentQuestionIndex).setIsAnswered(true);
            }
        }
        long nextQuestionId = currentQuestionIndex < questionsForCurrentQuiz.size() ? currentQuestionIndex + 2 : currentQuestionIndex;

        return "redirect:runquiz/" + quiz.getId() + "/" + nextQuestionId;
    }

    @RequestMapping(value = "submit")//TODO Подправить валидацию
    public String submitQuiz(ModelMap model, Principal principal) {
        addHeaderAttributes(model);

        user = userService.getUser(principal);

        List<UserAnswer> allAnswers = userAnswerService.findByUserAndQuiz(user, quiz);
        int averageScore = 0;

        if (!allAnswers.isEmpty()) {
            int allScore = 0;
            for (UserAnswer userAnswer : allAnswers) {
                allScore += userAnswer.getScore();
            }

            averageScore = allScore / questionsForCurrentQuiz.size();
            quiz.setScore(averageScore);
            quizService.updateQuiz(quiz);
        }
        model.addAttribute("averageScore", averageScore);
        model.addAttribute("questionsInQuiz", questionsForCurrentQuiz);
        return "quiz/submitedQuiz";
    }


    private void deleteExistAnswersToQuestionInDb() {
        List<UserAnswer> existAnswers = userAnswerService.findByUserAndQuestionAndQuiz(user, getCurrentQuestionForQuiz(currentQuestionIndex).getQuestion(), quiz);

        for (UserAnswer userAnswer : existAnswers) {
            userAnswerService.removeUserAnswer(userAnswer);
        }
        getCurrentQuestionForQuiz(currentQuestionIndex).setScore(0);
    }

    private QuestionsForQuiz getCurrentQuestionForQuiz(Long q) {
        return questionsForCurrentQuiz.get(q.intValue());
    }

    private long getCurrentQuestionId(Long q) {
        if (q < 0)
            throw new IllegalArgumentException();
        if (q >= questionsForCurrentQuiz.size())
            return questionsForCurrentQuiz.size() - 1;
        return q - 1;

    }


}
