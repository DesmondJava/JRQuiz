package com.jrquiz.repository;

import com.jrquiz.entity.Question;
import com.jrquiz.entity.Quiz;
import com.jrquiz.entity.User;
import com.jrquiz.entity.statistics.UserAnswer;
import com.jrquiz.entity.Tag;
import com.jrquiz.support.stat.TagStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long>
{
    List<UserAnswer> findByUser(User user);
    List<UserAnswer> findByQuiz(Quiz quiz);
    List<UserAnswer> findByUserAndQuiz(User user, Quiz quiz);
    @Query("select ua from UserAnswer ua " +
            "where ua.user = :user and ua.quiz = :quiz and ua.answer is null " +
            "order by questionIndex")
    List<UserAnswer> findByUserAndQuizAndAnswerIsNullOrderByQuestionIndex(@Param("user") User user, @Param("quiz") Quiz quiz);
    @Query("select ua from UserAnswer ua " +
            "where ua.user = :user and ua.answer is null " +
            "order by questionIndex")
    List<UserAnswer> findByUserAndAnswerIsNullOrderByQuestionIndex(@Param("user") User user);

    @Query("select distinct ua.quiz from UserAnswer ua " +
            "where ua.user = :user and ua.answer is null ")
    List<UserAnswer> findByUserAndAnswerIsNullDistinctQuiz(@Param("user") User user);

    List<UserAnswer> findByQuestion(Question question);

    @Query("select ua from UserAnswer ua " +
            "where ua.user = :user and ua.score = :score ")
    List<UserAnswer> findByUserAndScore(@Param("user")User user, @Param ("score")int score);

    List<UserAnswer> findByUserAndQuestionAndQuiz(User user, Question question, Quiz quiz);

    @Query(value = "SELECT SUM(uq) FROM (SELECT COUNT(DISTINCT question_id) as uq FROM stat_users_answers WHERE user_id = :user  GROUP BY quiz_id) as T" , nativeQuery = true)
    Integer countOfAnsweredQuestionsByUser(@Param("user")User user);

    @Query(value = "SELECT SUM(uq) FROM (SELECT COUNT(DISTINCT question_id) as uq FROM stat_users_answers WHERE user_id = :user and score = 100 GROUP BY quiz_id) as T" , nativeQuery = true)
    Integer countOfCorrectAnsweredQuestionsByUser(@Param("user") User user);

    @Query(value = "select count(ua.question) from  UserAnswer ua where ua.user = :user and ua.quiz = :quiz")
    Integer countOfAnsweredQuestionsByUserAndQuiz(@Param("user")User user, @Param("quiz")Quiz quiz);

    @Query(value = "select new com.jrquiz.support.stat.TagStatistic(t, count(distinct q)) from Tag as t" +
            "        join t.questions as q" +
            "        join q.userAnswers as ua" +
            "        where ua.score = 100 and ua.user = :user" +
            "        group by t")
    List<TagStatistic> statisticOfPassingQuestionsByTag(@Param("user") User user);
}
