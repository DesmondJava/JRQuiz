package com.jrquiz.controller;

import com.jrquiz.entity.Quiz;
import com.jrquiz.entity.User;
import com.jrquiz.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController extends TemplateController {

    @Autowired
    QuestionService questionService;

    @Autowired
    QuizService quizService;

    @Autowired
    TagService tagService;

    @Autowired
    UserService userService;

    @Autowired
    UserAnswerService userAnswerService;

    private User user;

    @RequestMapping("/hello")
    public String startPage(ModelMap model, Principal principal) {
        addHeaderAttributes(model);
        List<Quiz> uncompletedQuizzes = new ArrayList<>(quizService.findUncompletedQuizzesByUser(userService.getUser(principal)));
        List<Integer> progressList = new ArrayList<>();

        for (Quiz quiz : uncompletedQuizzes) {
            Double allQuestions = (double) quiz.getQuestions().size();
            Double answeredQuestions = (double) userAnswerService.countOfAnsweredQuestionsByQuiz(userService.getUser(principal), quiz);

            Integer progress = (int)(100 - ((allQuestions - answeredQuestions) / allQuestions) * 100);
            progressList.add(progress);
        }

        model.addAttribute("allTags", tagService.getAllTags());
        model.addAttribute("allUncompletedQuizzes", uncompletedQuizzes);
        model.addAttribute("progressList", progressList);
        return "interface/onStart";
    }

    @RequestMapping("/user")
    public String startUserInterface(ModelMap model) {
        addHeaderAttributes(model);
        model.addAttribute("getTest", quizService.getQuiz(1l));
        return "interface/user";
    }

    @RequestMapping("/admin")
    public String startAdminInterface(ModelMap model) {
        addHeaderAttributes(model);
        model.addAttribute("questionsAmount", questionService.getAmountOfQuestions());
        model.addAttribute("questionActive", questionService.getActiveTrue());
        return "interface/admin";
    }

}
