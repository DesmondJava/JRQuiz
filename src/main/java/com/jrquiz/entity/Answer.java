package com.jrquiz.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "answer")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 6, nullable = false)
    private Long id;

    @Column(name = "answertext", nullable = false)
    private String answerText;

    @Column(name = "isCorrect", nullable = false)
    @Type(type = "yes_no")
    private Boolean isCorrect = false;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Answer answer = (Answer) o;

        if (answerText != null ? !answerText.equals(answer.answerText) : answer.answerText != null) return false;
        //if (isCorrect != null ? !isCorrect.equals(answer.isCorrect) : answer.isCorrect != null) return false;
        if (question != null ? !question.equals(answer.question) : answer.question != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = answerText != null ? answerText.hashCode() : 0;
        //result = 31 * result + (isCorrect != null ? isCorrect.hashCode() : 0);
        result = 31 * result + (question != null ? question.hashCode() : 0);
        return result;
    }
}
