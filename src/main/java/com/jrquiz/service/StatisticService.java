package com.jrquiz.service;


import com.jrquiz.entity.Question;
import com.jrquiz.entity.Tag;
import com.jrquiz.entity.User;
import com.jrquiz.support.stat.TagStatistic;

import java.util.List;

public interface StatisticService {
    public int passedQuizzes(User user);
    public int getAmountQuizzes();
    public int answeredQuestions(User user);
    public int correctAnsweredQuestions(User user);
    public Tag getPopularTag(User user);
    public List<TagStatistic> getTagStatistic(User user);
    public int getPointsGained(User user);
    public int getAmountPoints();
    public int getAmountQuestions();

}
