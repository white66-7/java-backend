package com.example.demo.controller;

import com.example.demo.model.ContributionCalendar;
import com.example.demo.entity.GithubCommit;
import com.example.demo.repository.GithubCommitRepository;
import com.example.demo.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*")
public class ContributionController {

    @Autowired
    private GitHubService gitHubService; // 你原来用于查询日历的 Service

    @Autowired
    private GithubCommitRepository commitRepository; // 注入刚才写的接口

    @Value("${github.username}")
    private String username;

    // ==================== 1. 原有接口：贡献日历 ====================
    @GetMapping("/contributions")
    public ResponseEntity<ContributionCalendar> getContributions() {
        try {
            ContributionCalendar calendar = gitHubService.fetchContributions();
            return ResponseEntity.ok(calendar);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==================== 2. 原有接口：用户统计 ====================
    @GetMapping("/user-stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        try {
            String url = "https://api.github.com/users/" + username;
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> data = restTemplate.getForObject(url, Map.class);

            Map<String, Object> result = new HashMap<>();
            result.put("public_repos", data.get("public_repos"));
            result.put("followers", data.get("followers"));
            result.put("created_at", data.get("created_at"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==================== 3. 新增接口：直接从 H2 数据库拿 Commit 数据 ====================
    @GetMapping("/commits-timeline")
    public ResponseEntity<List<GithubCommit>> getCommitsTimeline() {
        try {
            List<GithubCommit> commits = commitRepository.findAllByOrderByCommitDateDesc();
            return ResponseEntity.ok(commits);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}