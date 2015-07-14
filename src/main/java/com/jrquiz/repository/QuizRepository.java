package com.jrquiz.repository;

import com.jrquiz.entity.Quiz;
import com.jrquiz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Set<Quiz> findByUser(User user);
    Set<Quiz> findByUserAndScoreLessThan(User user, Integer score);
    int countByUserAndScoreGreaterThan(User user, Integer score);
}
