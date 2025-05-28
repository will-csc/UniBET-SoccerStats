package com.futweb.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_game")
    private Integer gameId;

    @Column(name = "game_day", nullable = false)
    private LocalDate gameDate;

    @Column(name = "total_goals")
    private Integer totalGoals;

    @ManyToOne // "muitos objetos desta entidade apontam para um objeto de outra entidade".
    @JoinColumn(name = "home_id", nullable = false)
    private Team homeTeam;

    @Column(name = "home_goals")
    private Integer homeGoals;

    @Column(name = "home_corners")
    private Integer homeCorners;

    @Column(name = "home_shots")
    private Integer homeShots;

    @ManyToOne //"muitos objetos desta entidade apontam para um objeto de outra entidade".
    @JoinColumn(name = "away_id", nullable = false)
    private Team awayTeam;

    @Column(name = "away_goals")
    private Integer awayGoals;

    @Column(name = "away_corners")
    private Integer awayCorners;

    @Column(name = "away_shots")
    private Integer awayShots;

    @Column(name = "game_importance", length = 10)
    private String gameImportance;

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public LocalDate getGameDate() {
        return gameDate;
    }

    public void setGameDate(LocalDate gameDate) {
        this.gameDate = gameDate;
    }

    public Integer getTotalGoals() {
        return totalGoals;
    }

    public void setTotalGoals(Integer totalGoals) {
        this.totalGoals = totalGoals;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Integer getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(Integer homeGoals) {
        this.homeGoals = homeGoals;
    }

    public Integer getHomeCorners() {
        return homeCorners;
    }

    public void setHomeCorners(Integer homeCorners) {
        this.homeCorners = homeCorners;
    }

    public Integer getHomeShots() {
        return homeShots;
    }

    public void setHomeShots(Integer homeShots) {
        this.homeShots = homeShots;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Integer getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(Integer awayGoals) {
        this.awayGoals = awayGoals;
    }

    public Integer getAwayCorners() {
        return awayCorners;
    }

    public void setAwayCorners(Integer awayCorners) {
        this.awayCorners = awayCorners;
    }

    public Integer getAwayShots() {
        return awayShots;
    }

    public void setAwayShots(Integer awayShots) {
        this.awayShots = awayShots;
    }

    public String getGameImportance() {
        return gameImportance;
    }

    public void setGameImportance(String gameImportance) {
        this.gameImportance = gameImportance;
    }

}