package com.example.demo.controller;

import com.example.demo.model.ContributionCalendar;
import com.example.demo.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*")
public class ContributionController {

    @Autowired
    private GitHubService gitHubService;

    @GetMapping("/contributions")
    public ResponseEntity<ContributionCalendar> getContributions() {
        try {
            ContributionCalendar calendar = gitHubService.fetchContributions();
            return ResponseEntity.ok(calendar);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}