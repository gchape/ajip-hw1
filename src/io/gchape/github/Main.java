package io.gchape.github;

import io.gchape.github.file.FileUtils;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            FileUtils.sort("sonnets.txt", String::compareToIgnoreCase);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}