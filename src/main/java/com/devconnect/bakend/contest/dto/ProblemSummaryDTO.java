package com.devconnect.bakend.contest.dto;




import com.devconnect.bakend.contest.Difficulty;
import com.devconnect.bakend.contest.Problem;
import lombok.*;

@Data
@Builder
public class ProblemSummaryDTO {
    private Long id;
    private String title;
    private Difficulty difficulty;
    private Integer points;

    public static ProblemSummaryDTO from(Problem problem) {
        return ProblemSummaryDTO.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .points(problem.getPoints())
                .build();
    }
}