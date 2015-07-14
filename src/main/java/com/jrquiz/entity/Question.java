package com.jrquiz.entity;

import com.jrquiz.entity.enums.QuestionType;
import com.jrquiz.entity.statistics.UserAnswer;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", unique = true, nullable = false, length = 512)
    private String title;

    @Column(name = "text", nullable = false, length = 4096)
    private String text;

    @Column(name = "complexity", length = 1)
    private Byte complexity;

    @Column(name = "timetosolve")
    private Integer timeToSolve;

    @Column(name = "questiontype")
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Answer> answers;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserAnswer> userAnswers;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Comment> comments;

    @Column(name = "createdate")
    private Date createDate;

    @Column(name = "modifieddate")
    private Date modifiedDate;

    @Column(name = "active")
    @Type(type = "yes_no")
    private Boolean active = true;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "createdbyuser_id")
    private User createdBy;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "modifiedbyuser_id")
    private User modifiedBy;

    @ManyToMany(cascade = { CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinTable(name = "questiontag",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    @ManyToMany(mappedBy = "questions", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    //TODO check cascade for delete
    private Set<Quiz> quizzes;

    public Set<Quiz> getQuiz() {
        return quizzes;
    }

    public void setQuiz(Set<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Question() {
        this.createDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Byte getComplexity() {
        return complexity;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setComplexity(Byte complexity) {
        this.complexity = complexity;
    }

    public Integer getTimeToSolve() {
        return timeToSolve;
    }

    public void setTimeToSolve(Integer timeToSolve) {
        this.timeToSolve = timeToSolve;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (id != null ? !id.equals(question.id) : question.id != null) return false;
        if (title != null ? !title.equals(question.title) : question.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
