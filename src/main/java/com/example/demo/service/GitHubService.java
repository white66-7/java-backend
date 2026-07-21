package com.example.demo.service;

import com.example.demo.model.ContributionCalendar;
import com.example.demo.model.ContributionDay;
import com.example.demo.model.Week;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

    @Value("${github.token}")
    private String token;

    @Value("${github.username}")
    private String username;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public ContributionCalendar fetchContributions() {
        String query = """
            query($username: String!) {
                user(login: $username) {
                    contributionsCollection {
                        contributionCalendar {
                            totalContributions
                            weeks {
                                contributionDays {
                                    contributionCount
                                    date
                                }
                            }
                        }
                    }
                }
            }
            """;

        Map<String, Object> variables = Map.of("username", username);
        Map<String, Object> body = Map.of("query", query, "variables", variables);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.github.com/graphql",
                requestEntity,
                Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        Map<String, Object> user = (Map<String, Object>) data.get("user");
        Map<String, Object> contributionsCollection = (Map<String, Object>) user.get("contributionsCollection");
        Map<String, Object> calendarMap = (Map<String, Object>) contributionsCollection.get("contributionCalendar");

        // 构建 ContributionCalendar 对象
        ContributionCalendar calendar = new ContributionCalendar();
        calendar.setTotalContributions((Integer) calendarMap.get("totalContributions"));

        List<Map<String, Object>> weeksRaw = (List<Map<String, Object>>) calendarMap.get("weeks");
        List<Week> weeks = new ArrayList<>();

        for (Map<String, Object> weekRaw : weeksRaw) {
            Week week = new Week();
            List<Map<String, Object>> daysRaw = (List<Map<String, Object>>) weekRaw.get("contributionDays");
            List<ContributionDay> days = new ArrayList<>();

            for (Map<String, Object> dayRaw : daysRaw) {
                ContributionDay day = new ContributionDay();
                day.setContributionCount((Integer) dayRaw.get("contributionCount"));
                day.setDate((String) dayRaw.get("date"));
                days.add(day);
            }
            week.setContributionDays(days);
            weeks.add(week);
        }
        calendar.setWeeks(weeks);
        return calendar;
    }
}