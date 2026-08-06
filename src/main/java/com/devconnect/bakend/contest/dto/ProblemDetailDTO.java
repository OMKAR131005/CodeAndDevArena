package com.devconnect.bakend.contest.dto;


import com.devconnect.bakend.contest.Difficulty;
import com.devconnect.bakend.contest.Problem;
import com.devconnect.bakend.contest.TestCase;
import lombok.*;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ProblemDetailDTO {
    private Long id;
    private String title;
    private String statement;
    private String constraints;
    private Difficulty difficulty;
    private Integer points;
    private Integer timeLimitMs;
    private List<TestCaseDTO> sampleTestCases;

    public static ProblemDetailDTO from(Problem problem) {
        List<TestCaseDTO> samples = problem.getTestCases().stream()
                .filter(TestCase::isSample)
                .map(tc -> TestCaseDTO.builder()
                        .input(tc.getInput())
                        .expectedOutput(tc.getExpectedOutput())
                        .isSample(true)
                        .build())
                .collect(Collectors.toList());

        return ProblemDetailDTO.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .statement(problem.getStatement())
                .constraints(problem.getConstraints())
                .difficulty(problem.getDifficulty())
                .points(problem.getPoints())
                .timeLimitMs(problem.getTimeLimitMs())
                .sampleTestCases(samples)
                .build();
    }
}