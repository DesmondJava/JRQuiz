package com.jrquiz.service;

import com.jrquiz.entity.Answer;

public interface AnswerService {

    Answer addAnswer(Answer answer);

    Answer getAnswer(Long id);

    Answer updateAnswer(Answer answer);

    void removeAnswer(Long id);
}
