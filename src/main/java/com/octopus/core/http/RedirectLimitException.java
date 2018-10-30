package com.octopus.core.http;

public class RedirectLimitException extends Exception {
    public RedirectLimitException(int exceptions) {
        super(System.out.printf("exceeded maximum %d redirects", exceptions).toString());
    }
}
