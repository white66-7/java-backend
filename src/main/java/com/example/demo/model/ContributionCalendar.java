package com.example.demo.model;

import java.util.List;

public class ContributionCalendar {
    private int totalContributions;
    private List<Week> weeks;

    public int getTotalContributions() {
        return totalContributions;
    }

    public void setTotalContributions(int totalContributions) {
        this.totalContributions = totalContributions;
    }

    public List<Week> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
    }
}