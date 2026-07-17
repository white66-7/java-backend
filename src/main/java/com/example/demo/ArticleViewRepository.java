package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleViewRepository extends JpaRepository<ArticleView, String> {
    // 这里不需要写任何方法，JpaRepository 已经提供了 findById、save 等
}