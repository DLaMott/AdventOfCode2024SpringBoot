package com.advent.AdventOfCode.helper;



import org.apache.commons.lang3.tuple.Pair;
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
    private final Map<Pair<Integer, Integer>, Function<Pair<Integer, Integer>, Day>> solvers = new HashMap<>();

    public DayProcess() {
        for (int day = 1; day <= 25; day++) {
            for (int part = 1; part <= 2; part++) {
                solvers.put(Pair.of(day, part), createSolverForDayPart(day, part));
            }
        }
    }

    private Function<Pair<Integer, Integer>, Day> createSolverForDayPart(int day, int part) {
        String methodName = "solveDay" + day + "Part" + part;
        try {
            var method = this.getClass().getDeclaredMethod(methodName, Integer.class, Integer.class);
            return dayPart -> {
                try {
                    return (Day) method.invoke(this, dayPart.getLeft(), dayPart.getRight());
                } catch (Exception e) {
                    throw new RuntimeException("Error invoking solver for " + methodName, e);
                }
            };
        } catch (NoSuchMethodException e) {
            // Use a lambda to call solveGenericDayPart with the Pair values
            return dayPart -> solveGenericDayPart(dayPart.getLeft(), dayPart.getRight());
        }
    }




    public ResponseEntity<Day> solveDayPart(int day, int part) {
        Pair<Integer, Integer> dayPart = Pair.of(day, part);
        Function<Pair<Integer, Integer>, Day> solver = solvers.get(dayPart);

        if (solver == null) {
            return ResponseEntity.badRequest()
                    .body(new Day("No solution available", "No input", "No answer"));
        }

        try {
            return ResponseEntity.ok(solver.apply(dayPart));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new Day("Error solving day " + day + ", part " + part, "Error", e.getMessage()));
        }
    }

    private Day solveDay1Part1(Integer day, Integer part) {
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

    private Day solveDay1Part2(Integer day, Integer part) {
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



            //store each in their own map with freq. Then mult the amount

            Map<Integer, Integer> left = new HashMap<>();
            Map<Integer,Integer> right = new HashMap<>();

            for(Integer i: sortedOne){

                left.put(i, left.getOrDefault(i,0) + 1);

            }

            for(Integer i: sortedTwo){

                right.put(i, right.getOrDefault(i,0) + 1);

            }

            int sum = 0;

            // iterate through both map to see how often left is in the right
            for(Map.Entry<Integer, Integer> map: left.entrySet()){
                if (right.containsKey(map.getKey())){
                    sum += map.getKey() * right.get(map.getKey());
                }

            }


            String puzzleAnswer = String.valueOf(sum);

            return new Day(puzzleText, puzzleInput, puzzleAnswer);
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 2", e.getMessage(), "N/A");
        }
    }

    private Day solveGenericDayPart(Integer day, Integer part) {
        return new Day("Generic Puzzle Text for Day " + day + ", Part " + part,
                "Generic Input",
                "Generic Solution");
    }
}
