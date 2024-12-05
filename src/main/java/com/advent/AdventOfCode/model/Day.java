package com.advent.AdventOfCode.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Day {

    public String day;
    public String input;
    public String answer;

    public Day() {
    }

    public Day(String day, String input, String answer) {
        this.day = day;
        this.input = input;
        this.answer = answer;
    }
}

