package com.jrquiz.repository;

import com.jrquiz.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    public Page<Question> findByOrderByCreateDateDesc(Pageable p);
    public Page<Question> findByOrderByCreateDateAsc(Pageable p);

    public Page<Question> findByOrderByIdAsc(Pageable p);
    public Page<Question> findByOrderByIdDesc(Pageable p);

    public Page<Question> findByOrderByTitleAsc(Pageable p);
    public Page<Question> findByOrderByTitleDesc(Pageable p);

    public Page<Question> findByOrderByCreatedByAsc(Pageable p);
    public Page<Question> findByOrderByCreatedByDesc(Pageable p);

    public Page<Question> findByOrderByComplexityAsc(Pageable p);
    public Page<Question> findByOrderByComplexityDesc(Pageable p);


    List<Question> findByTitleLike(String title);
    List<Question> findByActiveTrue();

    Long countByActiveTrue();
}