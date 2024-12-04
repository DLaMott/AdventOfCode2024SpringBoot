package com.advent.AdventOfCode.model;

public class Day {

    public String day;
    public String input;
    public String answer;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Day(){}

    public Day(String day, String input, String answer) {
        this.day = day;
        this.input = input;
        this.answer = answer;
    }



    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
