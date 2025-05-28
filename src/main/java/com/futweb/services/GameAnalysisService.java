package com.futweb.services;

import com.futweb.models.Game;

import java.util.List;

public class GameAnalysisService {

    public double calcularForcaOfensiva(List<Game> jogos, int teamId) {
        int totalGols = 0;
        int totalChutes = 0;
        int totalEscanteios = 0;
        int jogosConsiderados = 0;

        for (Game jogo : jogos) {
            if (jogo.getHomeTeam().getTeamId() == teamId) {
                totalGols += jogo.getHomeGoals();
                totalChutes += jogo.getHomeShots();
                totalEscanteios += jogo.getHomeCorners();
                jogosConsiderados++;
            } else if (jogo.getAwayTeam().getTeamId() == teamId) {
                totalGols += jogo.getAwayGoals();
                totalChutes += jogo.getAwayShots();
                totalEscanteios += jogo.getAwayCorners();
                jogosConsiderados++;
            }
        }

        if (jogosConsiderados == 0) return 0.0;

        double mediaGols = (double) totalGols / jogosConsiderados;
        double mediaChutes = (double) totalChutes / jogosConsiderados;
        double mediaEscanteios = (double) totalEscanteios / jogosConsiderados;

        // Pode ajustar os pesos conforme preferir
        return mediaGols * 0.5 + mediaChutes * 0.3 + mediaEscanteios * 0.2;
    }

    public double calcularForcaDefensiva(List<Game> jogos, int teamId) {
        int golsSofridos = 0;
        int chutesSofridos = 0;
        int escanteiosCedidos = 0;
        int jogosConsiderados = 0;

        for (Game jogo : jogos) {
            if (jogo.getHomeTeam().getTeamId() == teamId) {
                golsSofridos += jogo.getAwayGoals();
                chutesSofridos += jogo.getAwayShots();
                escanteiosCedidos += jogo.getAwayCorners();
                jogosConsiderados++;
            } else if (jogo.getAwayTeam().getTeamId() == teamId) {
                golsSofridos += jogo.getHomeGoals();
                chutesSofridos += jogo.getHomeShots();
                escanteiosCedidos += jogo.getHomeCorners();
                jogosConsiderados++;
            }
        }

        if (jogosConsiderados == 0) return 0.0;

        double mediaGolsSofridos = (double) golsSofridos / jogosConsiderados;
        double mediaChutesSofridos = (double) chutesSofridos / jogosConsiderados;
        double mediaEscanteiosCedidos = (double) escanteiosCedidos / jogosConsiderados;

        // Quanto menor, melhor. Invertendo a lógica com 1 / x.
        return 1.0 / (mediaGolsSofridos * 0.5 + mediaChutesSofridos * 0.3 + mediaEscanteiosCedidos * 0.2);
    }
}

