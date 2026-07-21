package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "github_commit")
public class GithubCommit {

    @Id
    private String sha; // 使用 commit 的哈希值作为主键，天然防重复

    private String repoName; // 仓库名称

    @Column(length = 2000) // 描述可能比较长，设大一点
    private String message;

    private Instant commitDate; // 提交时间

    private String url; // 跳转链接

    // ===== 生成 Getter 和 Setter =====
    public String getSha() { return sha; }
    public void setSha(String sha) { this.sha = sha; }

    public String getRepoName() { return repoName; }
    public void setRepoName(String repoName) { this.repoName = repoName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getCommitDate() { return commitDate; }
    public void setCommitDate(Instant commitDate) { this.commitDate = commitDate; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}