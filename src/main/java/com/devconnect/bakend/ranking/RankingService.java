package com.devconnect.bakend.ranking;

import com.devconnect.bakend.follow.FollowRepository;
import com.devconnect.bakend.follow.FollowStatus;
import com.devconnect.bakend.post.PostRepository;
import com.devconnect.bakend.profile.Profile;
import com.devconnect.bakend.profile.ProfileRepository;
import com.devconnect.bakend.profile.ProfileViewRepository;
import com.devconnect.bakend.ranking.dto.RankingResponse;
import com.devconnect.bakend.user.User;
import com.devconnect.bakend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class RankingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;
    private final ProfileViewRepository profileViewRepository;
    private final WebClient.Builder webClientBuilder;

    private static final String RANKING_KEY = "developer:ranking";
    private static final String EXT_SCORE_PREFIX = "ext_score:";


    public void updateRank(User user) {
        Profile profile = profileRepository.findByUser(user);
        if (profile == null) return;

        double dbScore = calculateDbScore(user, profile);


        Object extScoreObj = redisTemplate.opsForValue().get(EXT_SCORE_PREFIX + user.getUsername());
        double extScore = extScoreObj != null ? Double.parseDouble(extScoreObj.toString()) : 0.0;

        redisTemplate.opsForZSet().add(RANKING_KEY, user.getUsername(), dbScore + extScore);
    }


    private double calculateDbScore(User user, Profile profile) {
        double score = 0;

        long postCount = postRepository.countByUser(user);
        long followerCount = followRepository.countByFollowingAndStatus(user, FollowStatus.FOLLOWING);
        long totalViews = profileViewRepository.totalCountViews(profile);
        score += (postCount * 10) + (followerCount * 5) + (totalViews * 2);

        if (profile.getCgpa() != null) score += profile.getCgpa() * 50;
        if (profile.getPortfolioUrl() != null) score += 15;

        if (profile.getGithubHandle() != null) score += 10;
        if (profile.getLeetcodeHandle() != null) score += 10;
        if (profile.getCodeforcesHandle() != null) score += 10;
        if (profile.getGfgHandle() != null) score += 5;
        if (profile.getCodechefHandle() != null) score += 5;
        if (profile.getAtcoderHandle() != null) score += 5;
        if (profile.getHackerRank() != null) score += 5;

        return score;
    }


    @Scheduled(cron = "0 0 2 * * SUN")
    public void refreshExternalScores() {
        log.info("Starting weekly external score refresh...");
        int pageSize = 50;
        int page = 0;
        int totalProcessed = 0;

        while (true) {
            List<User> batch = userRepository.findAll(
                    PageRequest.of(page++, pageSize)
            ).getContent();

            if (batch.isEmpty()) break;


            batch.parallelStream().forEach(user -> {
                try {
                    Profile profile = profileRepository.findByUser(user);
                    if (profile == null) return;

                    double extScore = fetchExternalScore(profile);
                    redisTemplate.opsForValue().set(
                            EXT_SCORE_PREFIX + user.getUsername(),
                            String.valueOf(extScore)
                    );


                    double dbScore = calculateDbScore(user, profile);
                    redisTemplate.opsForZSet().add(RANKING_KEY, user.getUsername(), dbScore + extScore);

                } catch (Exception e) {
                    log.warn("External score failed for: " + user.getUsername());
                }
            });

            totalProcessed += batch.size();
            log.info("Processed {} users so far...", totalProcessed);


            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("Weekly external score refresh complete. Total: {}", totalProcessed);
    }


    @Scheduled(cron = "0 0 * * * *")
    public void refreshAllRankings() {
        log.info("Starting hourly DB score refresh...");
        List<User> allUsers = userRepository.findAll();

        allUsers.parallelStream().forEach(user -> {
            try {
                Profile profile = profileRepository.findByUser(user);
                if (profile == null) return;

                double dbScore = calculateDbScore(user, profile);

                Object extScoreObj = redisTemplate.opsForValue().get(EXT_SCORE_PREFIX + user.getUsername());
                double extScore = extScoreObj != null ? Double.parseDouble(extScoreObj.toString()) : 0.0;

                redisTemplate.opsForZSet().add(RANKING_KEY, user.getUsername(), dbScore + extScore);
            } catch (Exception e) {
                log.warn("Ranking refresh failed for: " + user.getUsername());
            }
        });

        log.info("Hourly DB score refresh complete.");
    }


    private double fetchExternalScore(Profile profile) {
        double score = 0;


        if (profile.getGithubHandle() != null) {
            try {
                Map githubData = webClientBuilder.build()
                        .get()
                        .uri("https://api.github.com/users/" + profile.getGithubHandle())
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();
                if (githubData != null) {
                    score += ((Number) githubData.getOrDefault("public_repos", 0)).doubleValue() * 5;
                    score += ((Number) githubData.getOrDefault("followers", 0)).doubleValue() * 3;
                }
            } catch (Exception e) {
                log.warn("GitHub API failed for: {}", profile.getGithubHandle());
            }
        }


        if (profile.getLeetcodeHandle() != null) {
            try {
                Map leetcodeData = webClientBuilder.build()
                        .get()
                        .uri("https://leetcode-stats-api.herokuapp.com/" + profile.getLeetcodeHandle())
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();
                if (leetcodeData != null) {
                    score += ((Number) leetcodeData.getOrDefault("totalSolved", 0)).doubleValue() * 3;
                }
            } catch (Exception e) {
                log.warn("LeetCode API failed for: {}", profile.getLeetcodeHandle());
            }
        }


        if (profile.getCodeforcesHandle() != null) {
            try {
                Map cfData = webClientBuilder.build()
                        .get()
                        .uri("https://codeforces.com/api/user.info?handles=" + profile.getCodeforcesHandle())
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();
                if (cfData != null && "OK".equals(cfData.get("status"))) {
                    List result = (List) cfData.get("result");
                    if (result != null && !result.isEmpty()) {
                        Map userInfo = (Map) result.get(0);
                        score += ((Number) userInfo.getOrDefault("rating", 0)).doubleValue() * 0.1;
                    }
                }
            } catch (Exception e) {
                log.warn("Codeforces API failed for: {}", profile.getCodeforcesHandle());
            }
        }

        return score;
    }


    public List<RankingResponse> getTopRankings(int topN) {
        Set<Object> topUsers = redisTemplate.opsForZSet()
                .reverseRange(RANKING_KEY, 0, topN - 1);

        List<RankingResponse> result = new ArrayList<>();
        if (topUsers != null) {
            int rank = 1;
            for (Object username : topUsers) {
                Double score = redisTemplate.opsForZSet().score(RANKING_KEY, username);
                result.add(RankingResponse.builder()
                        .rank(rank++)
                        .username(username.toString())
                        .score(score != null ? score : 0.0)
                        .build());
            }
        }
        return result;
    }
}