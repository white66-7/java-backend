package com.example.demo.service;

import com.example.demo.entity.GithubCommit;
import com.example.demo.repository.GithubCommitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class GithubCommitSyncService {

    @Autowired
    private GithubCommitRepository commitRepository;

    @Value("${github.username}")
    private String username;

    @Value("${github.token}")
    private String token;

    // 项目启动时先执行一次，之后每天凌晨 2 点自动执行
    @PostConstruct
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncCommitsFromGithub() {
        System.out.println("🚀 [GitHub Sync] 开始从 GitHub 同步 Commits 到 H2 数据库...");
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        // 如果你的 token 为空或者占位符，就不设置 Authorization
        if (token != null && !token.isEmpty() && !token.equals("${GITHUB_TOKEN}")) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 1. 获取用户所有的公开仓库
            String reposUrl = "https://api.github.com/users/" + username + "/repos?per_page=100";
            ResponseEntity<List> reposResponse = restTemplate.exchange(reposUrl, HttpMethod.GET, entity, List.class);
            List<Map<String, Object>> repos = reposResponse.getBody();

            if (repos == null) {
                System.out.println("⚠️ [GitHub Sync] 未找到仓库数据");
                return;
            }

            int newCommitsCount = 0;

            // 2. 遍历仓库拉取 Commit
            for (Map<String, Object> repo : repos) {
                String repoName = (String) repo.get("name");
                int page = 1;
                boolean hasMore = true;

                while (hasMore) {
                    String commitsUrl = "https://api.github.com/repos/" + username + "/" + repoName +
                            "/commits?author=" + username + "&per_page=100&page=" + page;

                    try {
                        ResponseEntity<List> commitsResponse = restTemplate.exchange(commitsUrl, HttpMethod.GET, entity, List.class);
                        List<Map<String, Object>> commitItems = commitsResponse.getBody();

                        if (commitItems != null && !commitItems.isEmpty()) {
                            for (Map<String, Object> item : commitItems) {
                                String sha = (String) item.get("sha");

                                // 数据库里有了就不重复存了
                                if (commitRepository.existsById(sha)) {
                                    continue;
                                }

                                Map<String, Object> commitData = (Map<String, Object>) item.get("commit");
                                Map<String, Object> authorData = (Map<String, Object>) commitData.get("author");

                                GithubCommit newCommit = new GithubCommit();
                                newCommit.setSha(sha);
                                newCommit.setRepoName(repoName);
                                newCommit.setMessage((String) commitData.get("message"));
                                newCommit.setCommitDate(Instant.parse((String) authorData.get("date")));
                                newCommit.setUrl((String) item.get("html_url"));

                                commitRepository.save(newCommit);
                                newCommitsCount++;
                            }
                            // 如果返回的数据满100条，说明可能有下一页
                            if (commitItems.size() == 100) {
                                page++;
                            } else {
                                hasMore = false;
                            }
                        } else {
                            hasMore = false;
                        }
                    } catch (Exception e) {
                        System.err.println("❌ [GitHub Sync] 仓库 " + repoName + " 没有 Commits 或无权限: " + e.getMessage());
                        hasMore = false;
                    }
                }
            }
            System.out.println("✅ [GitHub Sync] 同步完成！本次新增存入了 " + newCommitsCount + " 条 Commit 数据。");

        } catch (Exception e) {
            System.err.println("❌ [GitHub Sync] 拉取仓库列表失败，可能是 Token 错误或达到限流。");
            e.printStackTrace();
        }
    }
}