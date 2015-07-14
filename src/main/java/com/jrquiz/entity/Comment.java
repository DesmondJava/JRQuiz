package com.jrquiz.entity;

import javax.persistence.*;

@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 6, nullable = false)
    private Long id;

    @Column(name = "text")
    private String text;

    @ManyToOne(cascade = CascadeType.ALL)//TODO check cascade
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)//TODO check cascade
    @JoinColumn(name = "question_id")
    private Question question;


}
