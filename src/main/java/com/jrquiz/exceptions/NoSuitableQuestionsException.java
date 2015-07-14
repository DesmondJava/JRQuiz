package com.jrquiz.exceptions;

import java.io.IOException;

public class NoSuitableQuestionsException  extends IOException {
    public NoSuitableQuestionsException(String message) {
        super(message);
    }
}
