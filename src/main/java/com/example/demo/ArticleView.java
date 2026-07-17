package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ArticleView {

    @Id
    private String postId;   // 文章ID，如 "5"
    private int views;       // 浏览次数

    // 必须有一个无参构造方法（JPA要求）
    public ArticleView() {}

    public ArticleView(String postId, int views) {
        this.postId = postId;
        this.views = views;
    }

    // Getter 和 Setter
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }
}