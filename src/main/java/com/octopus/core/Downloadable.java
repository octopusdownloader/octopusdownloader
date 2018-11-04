package com.octopus.core;

public interface Downloadable {
    void download() throws Exception;

    long receivedBytes();
}
