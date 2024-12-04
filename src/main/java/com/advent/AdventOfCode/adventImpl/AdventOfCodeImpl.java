package com.advent.AdventOfCode.adventImpl;

import com.advent.AdventOfCode.helper.DayProcess;
import com.advent.AdventOfCode.model.Day;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AdventOfCodeImpl implements AdventOfCode{
@Autowired
    private DayProcess dayProcess;

    @Override
    public ResponseEntity<Day> adventOfCode(Integer day) throws IOException, InterruptedException {
        if (day < 1 || day > 31) {
            return ResponseEntity.badRequest()
                    .body(new Day("Invalid day", "Day must be between 1 and 31", "No solution"));
        }

        return dayProcess.solveDay(day);

    }
}
