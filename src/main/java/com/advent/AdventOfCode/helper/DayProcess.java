package com.advent.AdventOfCode.helper;

import com.advent.AdventOfCode.model.Day;
import com.advent.AdventOfCode.util.Scraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class DayProcess {

    @Autowired
    private Scraper scraper;

    // Mapping days to their solution logic
    private final Map<Integer, Function<Integer, Day>> daySolvers = new HashMap<>();

    public DayProcess() {
        for (int i = 1; i <= 31; i++) {
            daySolvers.put(i, createSolverForDay(i));
        }
    }

    private Function<Integer, Day> createSolverForDay(int day) {
        try {
            // Look for a method named `solveDayX` where X is the day
            var method = this.getClass().getDeclaredMethod("solveDay" + day, Integer.class);
            return dayArg -> {
                try {
                    return (Day) method.invoke(this, dayArg);
                } catch (Exception e) {
                    throw new RuntimeException("Error invoking solver for day " + day, e);
                }
            };
        } catch (NoSuchMethodException e) {
            return this::solveGenericDay; // Use generic solver if no specific method exists
        }
    }

    public ResponseEntity<Day> solveDay(int day) {
        Function<Integer, Day> solver = daySolvers.get(day);
        if (solver == null) {
            return ResponseEntity.badRequest()
                    .body(new Day("No solution available", "No input", "No answer"));
        }


        Day result = solver.apply(day);
        return ResponseEntity.ok(result);
    }

    private Day solveDay1(Integer day) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);

            int[][] sortedPairs = Arrays.stream(puzzleInput.split("\n"))
                    .map(line -> line.split(" ", 2))
                    .map(pair -> new int[]{Integer.parseInt(pair[0]), Integer.parseInt(pair[1].trim())})
                    .toArray(int[][]::new);

            // Separate and sort the individual arrays
            int[] sortedOne = Arrays.stream(sortedPairs)
                    .mapToInt(pair -> pair[0])
                    .sorted()
                    .toArray();

            int[] sortedTwo = Arrays.stream(sortedPairs)
                    .mapToInt(pair -> pair[1])
                    .sorted()
                    .toArray();

            int sum = 0;
            for (int i = 0; i < sortedOne.length; i++) {
                sum += Math.abs(sortedOne[i] - sortedTwo[i]);
            }

            String puzzleAnswer = String.valueOf(sum); //  "1941353"

            return new Day(puzzleText, puzzleInput, puzzleAnswer);
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 1", e.getMessage(), "N/A");
        }
    }

    private Day solveDay2(Integer day) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);
            String puzzleAnswer = "Custom logic for Day 2 solution"; // Replace with real logic

            return new Day(puzzleText, puzzleInput, puzzleAnswer);
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 2", e.getMessage(), "N/A");
        }
    }

    private Day solveGenericDay(Integer day) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);
            String puzzleAnswer = "Generic solution logic for Day " + day; // Replace with actual logic

            return new Day(puzzleText, puzzleInput, puzzleAnswer);
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day " + day, e.getMessage(), "N/A");
        }
    }
}
