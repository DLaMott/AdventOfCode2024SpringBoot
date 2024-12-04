package com.advent.AdventOfCode.adventImpl;

import com.advent.AdventOfCode.model.Day;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping(value = "/2024/")
public interface AdventOfCode {

    @GetMapping(value = {"/day/{day}"}, produces = {"application/json"})
    ResponseEntity<Day> adventOfCode(@PathVariable(value = "day") Integer day) throws IOException, InterruptedException;



}
