package com.example.vaadinspringpoc.data.entity;

import lombok.Getter;
import lombok.ToString;

@ToString(of = "caption")
public enum GameResult {
    WHITE_WIN("White win", "W"),
    BLACK_WIN("Black win", "B"),
    DRAW("Draw", "D");

    @Getter
    private final String caption;
    @Getter
    private final String code;

    GameResult(String caption, String code) {
        this.caption = caption;
        this.code = code;
    }

    public static GameResult fromCode(String code) {
        for (GameResult value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid GameResult: " + code);
    }
}
