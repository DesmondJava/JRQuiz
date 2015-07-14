package com.jrquiz.controller;

import com.jrquiz.entity.User;
import com.jrquiz.service.StatisticService;
import com.jrquiz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;


@Controller

@RequestMapping("/statistics")
public class StatisticController extends TemplateController {

    @Autowired
    StatisticService statisticService;

    @Autowired
    UserService userService;

    private User user;

    @RequestMapping()
    public String viewStatistic(ModelMap model, Principal principal)
    {
        if (user == null) {
            user = userService.getUser(principal);
        }
        addHeaderAttributes(model);

        model.addAttribute("answeredQuestions", statisticService.answeredQuestions(user));
        model.addAttribute("amountQuestions", statisticService.getAmountQuestions());
        model.addAttribute("pointsGained", statisticService.getPointsGained(user));
        model.addAttribute("amountPoints", statisticService.getAmountPoints());
        double newDouble = (double) statisticService.getPointsGained(user) /
                (double)statisticService.answeredQuestions(user);
        double averageDifficult = new BigDecimal(newDouble).setScale(1, RoundingMode.UP).doubleValue(); //rounds up double
        model.addAttribute("averageDifficult", averageDifficult);
        int percentageOfCorrectAnswer = (int) ((
                        (double)statisticService.correctAnsweredQuestions(user)/
                                (double)statisticService.answeredQuestions(user))*100);
        model.addAttribute("percentageOfCorrectAnswer", percentageOfCorrectAnswer);
        model.addAttribute("passedQuizzes", statisticService.passedQuizzes(user));
        model.addAttribute("amountQuizzes", statisticService.getAmountQuizzes());
        model.addAttribute("tagStatistics",statisticService.getTagStatistic(user));
        return "stat/statistic";
    }

}
