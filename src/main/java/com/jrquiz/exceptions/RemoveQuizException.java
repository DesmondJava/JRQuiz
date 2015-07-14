package com.jrquiz.exceptions;

import java.io.IOException;


public class RemoveQuizException extends IOException {
    public RemoveQuizException(String message) {
        super(message);
    }

    public RemoveQuizException() {
    }
}
