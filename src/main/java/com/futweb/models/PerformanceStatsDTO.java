package com.futweb.models;

public class PerformanceStatsDTO {
    private int percentualAcertos;
    private TeamDTO melhorTime;
    private TeamDTO piorTime;

    public PerformanceStatsDTO(int percentualAcertos, TeamDTO melhorTime, TeamDTO piorTime) {
        this.percentualAcertos = percentualAcertos;
        this.melhorTime = melhorTime;
        this.piorTime = piorTime;
    }

    // Getters
    public int getPercentualAcertos() { return percentualAcertos; }
    public TeamDTO getMelhorTime() { return melhorTime; }
    public TeamDTO getPiorTime() { return piorTime; }
}

