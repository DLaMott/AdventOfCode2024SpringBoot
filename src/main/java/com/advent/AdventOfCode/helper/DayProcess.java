package com.advent.AdventOfCode.helper;



import com.advent.AdventOfCode.AdventOfCodeApplication;
import org.apache.commons.lang3.tuple.Pair;
import com.advent.AdventOfCode.model.Day;
import com.advent.AdventOfCode.util.Scraper;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class DayProcess {

    @Autowired
    private Scraper scraper;

    private static Logger logger = LoggerFactory.getLogger(DayProcess.class);

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


    /**
     *
     *90 91 93 96 93
     *3 5 7 10 11 11
     *35 37 39 42 46
     *
     * return a sum of safe lists
     * any two adjacent levels can differ by one or three
     * list can either always increase or decrease
     * @param day
     * @param part
     * @return
     */
    private Day solveDay2Part1(Integer day, Integer part) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);

            List<int[]> lists = Arrays.stream(puzzleInput.split("\n"))
                    .map(list -> Arrays.stream(list.split(" "))
                    .mapToInt(Integer::parseInt).toArray())
                    .toList();

            int sum = 0;

            for (int[] reports : lists) {
                boolean isSafe = true;

                boolean isIncreasing = reports[1] > reports[0];

                for (int x = 1; x < reports.length; x++) {
                    int diff = reports[x] - reports[x - 1];

                    if (Math.abs(diff) < 1 || Math.abs(diff) > 3) {
                        isSafe = false;
                        break;
                    }

                    if (isIncreasing && diff < 0 || !isIncreasing && diff > 0) {
                        isSafe = false;
                        break;
                    }
                }
                if (isSafe) {
                    sum++;
                }
            }

            return new Day(puzzleText, puzzleInput, String.valueOf(sum));
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 2", e.getMessage(), "N/A");
        }
    }

    /**
     *
     *90 91 93 96 93
     *3 5 7 10 11 11
     *35 37 39 42 46
     *
     * return a sum of safe lists
     * any two adjacent levels can differ by one or three
     * list can either always increase or decrease
     * @param day
     * @param part
     * @return
     */
    private Day solveDay2Part2(Integer day, Integer part) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);

            List<int[]> lists = Arrays.stream(puzzleInput.split("\n"))
                    .map(line -> Arrays.stream(line.split(" "))
                            .mapToInt(Integer::parseInt)
                            .toArray())
                    .toList();

            int safeCount = 0;

            for (int[] reports : lists) {
                if (isSafe(reports)) {

                    safeCount++;

                } else {

                    boolean dampenerWorks = false;
                    for (int i = 0; i < reports.length; i++) {

                        int[] modifiedReports = removeIndex(reports, i);

                        if (isSafe(modifiedReports)) {
                            dampenerWorks = true;
                            break;
                        }
                    }
                    if (dampenerWorks) {
                        safeCount++;
                    }
                }
            }
            return new Day(puzzleText, puzzleInput, String.valueOf(safeCount));
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 2", e.getMessage(), "N/A");
        }
    }

    /**
     * Accepts reports
     * @param reports elf reports?
     * @return if report is safe
     */
    private boolean isSafe(int[] reports) {

        boolean isIncreasing = reports[1] > reports[0];

        for (int x = 1; x < reports.length; x++) {
            int diff = reports[x] - reports[x - 1];
            if (Math.abs(diff) < 1 || Math.abs(diff) > 3) {
                return false;
            }
            if (isIncreasing && diff < 0 || !isIncreasing && diff > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes an item from an array by an index.
     * @param array the report
     * @param index the index
     * @return an array without the specified index
     */
    private int[] removeIndex(int[] array, int index) {
        int[] result = new int[array.length - 1];
        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != index) {
                result[j++] = array[i];
            }
        }
        return result;
    }


    private Day solveGenericDayPart(Integer day, Integer part) {
        return new Day("Generic Puzzle Text for Day " + day + ", Part " + part,
                "Generic Input",
                "Generic Solution");
    }
}
