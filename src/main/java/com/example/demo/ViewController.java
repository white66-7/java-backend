package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/views")
public class ViewController {

    @Autowired
    private ArticleViewRepository repository;

    // 1. 获取浏览量（GET）
    @GetMapping("/{postId}")
    public Map<String, Object> getView(@PathVariable String postId) {
        Map<String, Object> res = new HashMap<>();
        Optional<ArticleView> record = repository.findById(postId);
        int views = record.map(ArticleView::getViews).orElse(0);
        res.put("views", views);
        return res;
    }

    // 2. 增加浏览量（POST）
    @PostMapping("/{postId}/increment")
    public Map<String, Object> incrementView(@PathVariable String postId) {
        // 从数据库查找，没有就新建一个初始值
        ArticleView record = repository.findById(postId)
                .orElse(new ArticleView(postId, 0));

        // 浏览量 +1
        record.setViews(record.getViews() + 1);
        repository.save(record); // 保存到数据库

        Map<String, Object> res = new HashMap<>();
        res.put("views", record.getViews());
        return res;
    }
}