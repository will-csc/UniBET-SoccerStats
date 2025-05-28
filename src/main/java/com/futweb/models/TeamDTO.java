package com.futweb.models;

public class TeamDTO {
    private Integer teamId;
    private String teamName;
    private String imageTeam;
    private Long totalGreens;
    private Long totalReds;

    public TeamDTO(Integer teamId, String teamName, String imageTeam, Long totalGreens, Long totalReds) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.imageTeam = imageTeam;
        this.totalGreens = totalGreens;
        this.totalReds = totalReds;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getImageTeam() {
        return imageTeam;
    }

    public void setImageTeam(String imageTeam) {
        this.imageTeam = imageTeam;
    }

    public Long getTotalGreens() {
        return totalGreens;
    }

    public void setTotalGreens(Long totalGreens) {
        this.totalGreens = totalGreens;
    }

    public Long getTotalReds() {
        return totalReds;
    }

    public void setTotalReds(Long totalReds) {
        this.totalReds = totalReds;
    }
}
