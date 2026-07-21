package com.example.demo.repository;

import com.example.demo.entity.GithubCommit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GithubCommitRepository extends JpaRepository<GithubCommit, String> {
    // 按照时间倒序查询所有 commits
    List<GithubCommit> findAllByOrderByCommitDateDesc();
}