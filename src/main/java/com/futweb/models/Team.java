package com.futweb.models;

import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_team")
    private Integer teamId;

    @Column(name = "TeamName", unique = true, nullable = false, length = 20)
    private String teamName;

    @Column(name = "season_wins", columnDefinition = "INT DEFAULT 0")
    private Integer season_wins;

    @Column(name = "season_losses", columnDefinition = "INT DEFAULT 0")
    private Integer season_losses;

    @Column(name = "goals_scored", columnDefinition = "INT DEFAULT 0")
    private Integer goals_scored;

    @Column(name = "goals_conceded", columnDefinition = "INT DEFAULT 0")
    private Integer goals_conceded;

    @Column(name = "team_feeling", length = 4)
    private String teamFeeling;

    @Column(name = "table_stading")
    private Integer tableStading;

    @Column(name = "weak_performance")
    private Integer weakPerformance;

    @Column(name = "strong_performance")
    private Integer strongPerformance;

    @Column(name = "total_competitions")
    private Integer totalCompetitions;

    @Column(name = "motivation", length = 4)
    private String motivation;

    @Column(name = "team_image", length = 300)
    private String team_image;

    public Integer getTableStading() {
        return tableStading;
    }

    public void setTableStading(Integer tableStading) {
        this.tableStading = tableStading;
    }

    public Integer getWeakPerformance() {
        return weakPerformance != null ? weakPerformance : 50;
    }

    public void setWeakPerformance(Boolean win) {
        if (this.weakPerformance == null) {
            this.weakPerformance = 0;
        }
        if (win) {
            int result = this.weakPerformance + 10;
            this.weakPerformance = (result > 100) ? 100 : result;
        } else {
            int result = this.weakPerformance - 10;
            this.weakPerformance = (result < 0) ? 0 : result;
        }
    }

    public void setStrongPerformance(Boolean win) {
        if (this.strongPerformance == null) {
            this.strongPerformance = 0;
        }
        if (win) {
            int result = this.strongPerformance + 10;
            this.strongPerformance = (result > 100) ? 100 : result;
        } else {
            int result = this.strongPerformance - 10;
            this.strongPerformance = (result < 0) ? 0 : result;
        }
    }

    public Integer getStrongPerformance() {
        return strongPerformance != null ? strongPerformance : 50;
    }

    public Integer getTotalCompetitions() {
        return totalCompetitions;
    }

    public void setTotalCompetitions(Integer totalCompetitions) {
        this.totalCompetitions = totalCompetitions;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getTeam_image() {
        return team_image;
    }

    public void setTeam_image(String team_image) {
        this.team_image = team_image;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getSeason_wins() {
        if(season_wins != null){
            return 0;
        }
        return season_wins;
    }

    public void setSeason_wins(int i) {
        int result = this.season_wins + season_wins;
        if(result > 100){
            this.season_wins = 100;
        }else{
            this.season_wins += 10;
        }
    }

    public Integer getSeason_losses() {
        if(season_losses != null){
            return 0;
        }
        return season_losses;
    }

    public void setSeason_losses(Integer season_losses) {
        this.season_losses = season_losses;
    }

    public Integer getGoals_scored() {
        if(goals_scored == null){
            return 0;
        }
        return goals_scored;
    }

    public Integer getGoals_conceded() {
        if(goals_conceded == null){
            return 0;
        }
        return goals_conceded;
    }

    public void setGoals_scored(Integer goals_scored) {
        if(this.goals_scored == null){
            this.goals_scored = goals_scored;
        } else {
            this.goals_scored += goals_scored;
        }
    }

    public void setGoals_conceded(Integer goals_conceded) {
        if(this.goals_conceded == null){
            this.goals_conceded = goals_conceded;
        } else {
            this.goals_conceded += goals_conceded;
        }
    }

    public String getTeamFeeling() {
        return teamFeeling;
    }

    public void setTeamFeeling(String teamFeeling) {
        this.teamFeeling = teamFeeling;
    }
}
