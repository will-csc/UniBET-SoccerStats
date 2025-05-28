package com.futweb.models;

public class GameDTO {
    private Integer gameId;
    private String gameDate; // String no formato ISO (yyyy-MM-dd)
    private String homeTeam; // nome do time mandante
    private Integer homeGoals;
    private Integer homeCorners;
    private Integer homeShots;
    private String awayTeam; // nome do time visitante
    private Integer awayGoals;
    private Integer awayCorners;
    private Integer awayShots;
    private String importance;
    private String homeImage;
    private String awayImage;

    // Construtor que recebe Game e converte
    public GameDTO(Game game) {
        this.gameId = game.getGameId();
        this.gameDate = game.getGameDate().toString();
        this.homeTeam = game.getHomeTeam().getTeamName(); // supondo que Team tem getTeamName()
        this.homeGoals = game.getHomeGoals();
        this.homeCorners = game.getHomeCorners();
        this.homeShots = game.getHomeShots();
        this.awayTeam = game.getAwayTeam().getTeamName();
        this.awayGoals = game.getAwayGoals();
        this.awayCorners = game.getAwayCorners();
        this.awayShots = game.getAwayShots();
        this.importance = game.getGameImportance();
        this.homeImage = game.getHomeTeam().getTeam_image();
        this.awayImage = game.getAwayTeam().getTeam_image();
    }

    public String getHomeImage() {
        return homeImage;
    }

    public void setHomeImage(String homeImage) {
        this.homeImage = homeImage;
    }

    public String getAwayImage() {
        return awayImage;
    }

    public void setAwayImage(String awayImage) {
        this.awayImage = awayImage;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getGameDate() {
        return gameDate;
    }

    public void setGameDate(String gameDate) {
        this.gameDate = gameDate;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
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

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
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

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }
}
