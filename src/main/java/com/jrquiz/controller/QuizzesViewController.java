package com.jrquiz.controller;

import com.jrquiz.entity.Quiz;
import com.jrquiz.entity.User;
import com.jrquiz.exceptions.RemoveQuizException;
import com.jrquiz.service.QuizService;
import com.jrquiz.service.UserAnswerService;
import com.jrquiz.service.UserService;
import com.jrquiz.service.sample.SampleDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import javax.transaction.Transactional;
import java.util.*;

@Controller
@RequestMapping(value = "/uncompletedquizzes")
public class QuizzesViewController {

    @Autowired
    QuizService quizService;

    @Autowired
    UserAnswerService userAnswerService;

    @Autowired
    SampleDataGenerator sampleDataGenerator;

    @Autowired
    UserService userService;

    private User user;

    @RequestMapping("/show")
    public String showUncompletedQuizzes(ModelMap model) {

        User user = userService.getUser(1l);
        List<Quiz> uncompletedQuizs = new ArrayList<>(quizService.findUncompletedQuizzesByUser(user));
        List<Double> progressList = new ArrayList<>();

        for (Quiz quiz : uncompletedQuizs) {
            Double allQuestions = (double) quiz.getQuestions().size();
            Double answeredQuestions = (double) userAnswerService.findByUserAndQuiz(user, quiz).size(); //TODO Сделать в репозитарии запрос для получения количества

            Double progress = 100 - ((allQuestions - answeredQuestions) / allQuestions) * 100;
            progressList.add(progress);
        }

        model.addAttribute("allUncompletedQuizzes", uncompletedQuizs);
        model.addAttribute("progressList", progressList);
        return "quiz/uncompletedQuizzes";
    }

    @RequestMapping("/delete")
    public String deleteQuiz( @RequestParam(value = "id") Quiz quiz) {

        User user1 = getUser();
        if(quiz.getUser().equals(user1))
        {
            try {
                quizService.removeQuiz(quiz);
            } catch (RemoveQuizException e) {
                e.printStackTrace();
                //TODO обработать ексепшн
            }
        }
        return "redirect:show";
    }

    private User getUser() //Временная заглушка
    {
        if (user == null) {
            user = userService.getUser(1L);
        }
        return user;
    }

}


