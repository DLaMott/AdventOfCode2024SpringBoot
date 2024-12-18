package com.advent.AdventOfCode.helper;

import org.apache.commons.lang3.tuple.Pair;
import com.advent.AdventOfCode.model.Day;
import com.advent.AdventOfCode.util.Scraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Day solveDay1Part1(Integer day, Integer part) {
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

    public Day solveDay1Part2(Integer day, Integer part) {
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

            Map<Integer, Integer> left = new HashMap<>();
            Map<Integer, Integer> right = new HashMap<>();

            for (Integer i : sortedOne) {

                left.put(i, left.getOrDefault(i, 0) + 1);

            }

            for (Integer i : sortedTwo) {

                right.put(i, right.getOrDefault(i, 0) + 1);

            }

            int sum = 0;

            for (Map.Entry<Integer, Integer> map : left.entrySet()) {
                if (right.containsKey(map.getKey())) {
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
     * 90 91 93 96 93
     * 3 5 7 10 11 11
     * 35 37 39 42 46
     * <p>
     * return a sum of safe lists
     * any two adjacent levels can differ by one or three
     * list can either always increase or decrease
     *
     * @param day
     * @param part
     * @return
     */
    public Day solveDay2Part1(Integer day, Integer part) {
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
     * 90 91 93 96 93
     * 3 5 7 10 11 11
     * 35 37 39 42 46
     * <p>
     * return a sum of safe lists
     * any two adjacent levels can differ by one or three
     * list can either always increase or decrease
     *
     * @param day
     * @param part
     * @return
     */
    public Day solveDay2Part2(Integer day, Integer part) {
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
     *
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
     *
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

    public Day solveDay3Part1(Integer day, Integer part) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);

            Pattern pattern = Pattern.compile("mul\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");

            Matcher matcher = pattern.matcher(puzzleInput);

            int sum = 0;

            while (matcher.find()) {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));

                sum += x * y;
            }

            return new Day(puzzleText, puzzleInput, String.valueOf(sum));
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 3", e.getMessage(), "N/A");
        }
    }

    public Day solveDay3Part2(Integer day, Integer part) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);

            Pattern mulPattern = Pattern.compile("mul\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
            // Regular expression to capture do() and don't() instructions
            Pattern controlPattern = Pattern.compile("do\\(\\)|don't\\(\\)");

            Matcher matcher = Pattern.compile(controlPattern.pattern() + "|" + mulPattern.pattern()).matcher(puzzleInput);

            boolean isEnabled = true; // mul instructions start as enabled
            int sum = 0;

            while (matcher.find()) {
                String match = matcher.group();

                if (match.equals("do()")) {
                    isEnabled = true;
                } else if (match.equals("don't()")) {
                    isEnabled = false;
                } else if (match.startsWith("mul")) {
                    if (isEnabled) {
                        Matcher mulMatcher = mulPattern.matcher(match);
                        if (mulMatcher.matches()) {
                            int x = Integer.parseInt(mulMatcher.group(1));
                            int y = Integer.parseInt(mulMatcher.group(2));
                            sum += x * y;
                        }
                    }
                }
            }

            return new Day(puzzleText, puzzleInput, String.valueOf(sum));
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 3", e.getMessage(), "N/A");
        }
    }

    public Day solveDay4Part1(Integer day, Integer part) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);
            String word = "XMAS";
            int sum = 0;

            // Setup a grid
            String[] rows = puzzleInput.split("\n");
            int numRows = rows.length;
            int numCols = rows[0].length();
            char[][] grid = new char[numRows][numCols];

            for (int i = 0; i < numRows; i++) {
                grid[i] = rows[i].toCharArray();
            }

            // set up all directions
            int[][] directions = {
                    {0, 1}, //right
                    {1, 0}, //down
                    {0, -1}, //left
                    {-1, 0}, //up
                    {1, 1}, //downRight
                    {-1, -1},//upLeft
                    {1, -1},//downLeft
                    {-1, 1},//upRight
            };

            int gridRows = grid.length;
            int gridCols = grid[0].length;

            for (int x = 0; x < gridRows; x++) {
                for (int i = 0; i < gridCols; i++) {
                    for (int[] dir : directions) {
                        if (canFormWord(grid, word, x, i, dir[0], dir[1])) {
                            sum++;
                        }
                    }
                }
            }
            return new Day(puzzleText, puzzleInput, String.valueOf(sum));
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 4", e.getMessage(), "N/A");
        }
    }


    private boolean canFormWord(char[][] grid, String word, int startrow, int startcol, int x, int y){
        int row = startrow;
        int col = startcol;
        int rows = grid.length;
        int cols = grid[0].length;

        for(int i = 0; i <word.length(); i++){
            if(row < 0 || row >= rows || col < 0 || col >= cols || grid[row][col] != word.charAt(i)){
                return false;
            }
            row += x;
            col += y;
        }

        return true;
    }

    public Day solveDay4Part2(Integer day, Integer part) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);
            String pattern = "MAS";
            int sum = 0;

            // Setup a grid
            String[] rows = puzzleInput.split("\n");
            int numRows = rows.length;
            int numCols = rows[0].length();
            char[][] grid = new char[numRows][numCols];

            for (int i = 0; i < numRows; i++) {
                grid[i] = rows[i].toCharArray();
            }

            // Iterate through the grid to find X-MAS patterns
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    if (isXMas(grid, row, col, pattern)) {
                        sum++;
                    }
                }
            }

            return new Day(puzzleText, puzzleInput, String.valueOf(sum));
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 4", e.getMessage(), "N/A");
        }
    }

    private boolean isXMas(char[][] grid, int startRow, int startCol, String pattern) {
        int len = pattern.length();

        // Check for the top-left to bottom-right "MAS" (forward or backward)
        boolean downRight = matchesPattern(grid, startRow, startCol, pattern, 1, 1) ||
                matchesPattern(grid, startRow, startCol, new StringBuilder(pattern).reverse().toString(), 1, 1);

        // Check for the bottom-left to top-right "MAS" (forward or backward)
        boolean upRight = matchesPattern(grid, startRow + len - 1, startCol, pattern, -1, 1) ||
                matchesPattern(grid, startRow + len - 1, startCol, new StringBuilder(pattern).reverse().toString(), -1, 1);

        return downRight && upRight;
    }

    private boolean matchesPattern(char[][] grid, int startRow, int startCol, String pattern, int rowStep, int colStep) {
        int numRows = grid.length;
        int numCols = grid[0].length;
        int len = pattern.length();

        for (int i = 0; i < len; i++) {
            int row = startRow + i * rowStep;
            int col = startCol + i * colStep;

            if (row < 0 || row >= numRows || col < 0 || col >= numCols || grid[row][col] != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public Day solveDay5Part1(Integer day, Integer part) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);

            String[] sections = puzzleInput.split("\\n\\n");
            String[] rules = sections[0].split("\\n");
            String[] updates = sections[1].split("\\n");

            int sum = findMiddleSum(rules, updates);

            return new Day(puzzleText, puzzleInput, String.valueOf(sum));
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 3", e.getMessage(), "N/A");
        }
    }

    public static int findMiddleSum(String[] rules, String[] updates) {
        int totalMiddleSum = 0;

        // Process each update
        for (String update : updates) {
            List<Integer> updateList = parseUpdate(update);
            List<int[]> filteredRules = filterRules(rules, updateList);

            if (isValidUpdate(updateList, filteredRules)) {
                int middleIndex = updateList.size() / 2;
                totalMiddleSum += updateList.get(middleIndex);
            }
        }

        return totalMiddleSum;
    }

    public static List<Integer> parseUpdate(String update) {
        String[] parts = update.split(",");
        List<Integer> updateList = new ArrayList<>();

        for (String part : parts) {
            updateList.add(Integer.parseInt(part));
        }

        return updateList;
    }

    public static List<int[]> filterRules(String[] rules, List<Integer> updateList) {
        Set<Integer> updateSet = new HashSet<>(updateList);
        List<int[]> filteredRules = new ArrayList<>();

        for (String rule : rules) {
            String[] parts = rule.split("\\|");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            // Only include rules where both pages exist in the update
            if (updateSet.contains(x) && updateSet.contains(y)) {
                filteredRules.add(new int[]{x, y});
            }
        }

        return filteredRules;
    }

    public static boolean isValidUpdate(List<Integer> update, List<int[]> rules) {
        // Map page to its index in the update list
        Map<Integer, Integer> pageIndex = new HashMap<>();
        for (int i = 0; i < update.size(); i++) {
            pageIndex.put(update.get(i), i);
        }

        // Check all rules for validity
        for (int[] rule : rules) {
            int x = rule[0];
            int y = rule[1];

            // If x must come before y, check their indices
            if (pageIndex.get(x) > pageIndex.get(y)) {
                return false; // Rule violated
            }
        }

        return true;
    }
    public Day solveDay5Part2(Integer day, Integer part) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);

            String[] sections = puzzleInput.split("\\n\\n");
            String[] rules = sections[0].split("\\n");
            String[] updates = sections[1].split("\\n");

            int sum = findReorderedMiddleSum(rules, updates);


            return new Day(puzzleText, puzzleInput, String.valueOf(sum));
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 3", e.getMessage(), "N/A");
        }
    }

    public static int findReorderedMiddleSum(String[] rules, String[] updates) {
        int totalMiddleSum = 0;

        // Process each update
        for (String update : updates) {
            List<Integer> updateList = parseUpdate(update);
            List<int[]> filteredRules = filterRules(rules, updateList);

            if (!isValidUpdate(updateList, filteredRules)) {
                // Reorder the update correctly
                updateList = reorderUpdate(updateList, filteredRules);
                int middleIndex = updateList.size() / 2;
                totalMiddleSum += updateList.get(middleIndex);
            }
        }

        return totalMiddleSum;
    }

    public static List<Integer> reorderUpdate(List<Integer> update, List<int[]> rules) {
        // Create a graph and indegree map for the update
        Map<Integer, List<Integer>> graph = new HashMap<>();
        Map<Integer, Integer> indegree = new HashMap<>();

        for (int page : update) {
            graph.put(page, new ArrayList<>());
            indegree.put(page, 0);
        }

        for (int[] rule : rules) {
            int x = rule[0];
            int y = rule[1];
            graph.get(x).add(y);
            indegree.put(y, indegree.get(y) + 1);
        }

        // Perform topological sort
        Queue<Integer> queue = new LinkedList<>();
        for (int page : update) {
            if (indegree.get(page) == 0) {
                queue.add(page);
            }
        }

        List<Integer> sortedOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            int current = queue.poll();
            sortedOrder.add(current);

            for (int neighbor : graph.get(current)) {
                indegree.put(neighbor, indegree.get(neighbor) - 1);
                if (indegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return sortedOrder;
    }

    public Day solveDay6Part1(Integer day, Integer part) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);

            int sum = 0;

            return new Day(puzzleText, puzzleInput, String.valueOf(sum));
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 3", e.getMessage(), "N/A");
        }
    }

    public Day solveDay6Part2(Integer day, Integer part) {
        try {
            String puzzleText = scraper.fetchPuzzleDescription(day);
            String puzzleInput = scraper.fetchPuzzleInput(day);

            int sum = 0;

            return new Day(puzzleText, puzzleInput, String.valueOf(sum));
        } catch (IOException | InterruptedException e) {
            return new Day("Error fetching Day 3", e.getMessage(), "N/A");
        }
    }

    private Day solveGenericDayPart(Integer day, Integer part) {
        return new Day("Generic Puzzle Text for Day " + day + ", Part " + part,
                "Generic Input",
                "Generic Solution");
    }
}
