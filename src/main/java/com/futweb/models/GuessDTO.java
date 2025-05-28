package com.futweb.models;

import com.futweb.services.GameAnalysisService;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuessDTO {
    private String winner;
    private BigDecimal odd;
    private BigDecimal value;
    private String gameDate;
    private String probableWinner;
    private String winProbability;
    private String suggestedValue;
    private String unpredictability;
    private Integer guessId;
    private Integer winnerId;
    private Integer myTeamId;
    private String otherTeam;
    private String myTeam;
    private String winnerImage;

    public Integer getMyTeamId() {
        return myTeamId;
    }

    public void setMyTeamId(Integer myTeamId) {
        this.myTeamId = myTeamId;
    }

    public String getMyTeam() {
        return myTeam;
    }

    public void setMyTeam(String myTeam) {
        this.myTeam = myTeam;
    }

    public String getOtherTeam() {
        return otherTeam;
    }

    public void setOtherTeam(String otherTeam) {
        this.otherTeam = otherTeam;
    }

    public String getWinnerImage() {
        return winnerImage;
    }

    public void setWinnerImage(String winnerImage) {
        this.winnerImage = winnerImage;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }

    public void setProbableWinner(String probableWinner) {
        this.probableWinner = probableWinner;
    }

    public void setUnpredictability(String unpredictability) {
        this.unpredictability = unpredictability;
    }

    public Integer getGuessId() {
        return guessId;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public BigDecimal getOdd() {
        return odd;
    }

    public void setOdd(BigDecimal odd) {
        this.odd = odd;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getGameDate() {
        return gameDate;
    }

    public void setGameDate(String gameDate) {
        this.gameDate = gameDate;
    }

    public String getProbableWinner() {
        return probableWinner;
    }

    public String setProbableWinner(Team myTeam, Team otherTeam, Game game, List<Game> historicoJogos, Guess guess) {
        GameAnalysisService GameStatsAnalyzer = new GameAnalysisService();

        // ==== Separa confrontos diretos dos demais ====
        List<Game> headToHeadGames = historicoJogos.stream()
                .filter(g ->
                        (g.getHomeTeam().equals(myTeam) && g.getAwayTeam().equals(otherTeam)) ||
                                (g.getHomeTeam().equals(otherTeam) && g.getAwayTeam().equals(myTeam))
                )
                .sorted(Comparator.comparing(Game::getGameDate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<Game> jogosSemConfrontoDireto = historicoJogos.stream()
                .filter(g -> !headToHeadGames.contains(g))
                .collect(Collectors.toList());

        // ==== Força ofensiva/defensiva com jogos gerais ====
        double myAtaque = GameStatsAnalyzer.calcularForcaOfensiva(jogosSemConfrontoDireto, myTeam.getTeamId());
        double myDefesa = GameStatsAnalyzer.calcularForcaDefensiva(jogosSemConfrontoDireto, myTeam.getTeamId());

        double otherAtaque = GameStatsAnalyzer.calcularForcaOfensiva(jogosSemConfrontoDireto, otherTeam.getTeamId());
        double otherDefesa = GameStatsAnalyzer.calcularForcaDefensiva(jogosSemConfrontoDireto, otherTeam.getTeamId());

        // ==== WinRate geral ====
        int myWins = myTeam.getSeason_wins() != null ? myTeam.getSeason_wins() : 0;
        int myLosses = myTeam.getSeason_losses() != null ? myTeam.getSeason_losses() : 0;
        int otherWins = otherTeam.getSeason_wins() != null ? otherTeam.getSeason_wins() : 0;
        int otherLosses = otherTeam.getSeason_losses() != null ? otherTeam.getSeason_losses() : 0;

        int myTotalGames = myWins + myLosses;
        int otherTotalGames = otherWins + otherLosses;

        double myWinRate = myTotalGames > 0 ? (double) myWins / myTotalGames : 0;
        double otherWinRate = otherTotalGames > 0 ? (double) otherWins / otherTotalGames : 0;

        // ==== Performance contra fortes/fracos ====
        myWinRate += myTeam.getStrongPerformance() / 1000.0;
        myWinRate -= myTeam.getWeakPerformance() / 1000.0;

        otherWinRate += otherTeam.getStrongPerformance() / 1000.0;
        otherWinRate -= otherTeam.getWeakPerformance() / 1000.0;

        // ==== Posição na tabela ====
        int myStanding = myTeam.getTableStading();
        int otherStanding = otherTeam.getTableStading();

        if (myStanding > 0 && otherStanding > 0) {
            myWinRate += (20 - myStanding) / 100.0;
            otherWinRate += (20 - otherStanding) / 100.0;
        }

        // ==== Importância do jogo ====
        double importanceFactor = switch (game.getGameImportance().toUpperCase()) {
            case "ALTA" -> 1.05;
            case "REGULAR" -> 1.00;
            case "BAIXA" -> 0.95;
            default -> 1.00;
        };

        myWinRate *= importanceFactor;
        otherWinRate *= importanceFactor;

        // ==== Ajuste ofensivo/defensivo ====
        myWinRate += (myAtaque * 0.01) - (myDefesa * 0.01);
        otherWinRate += (otherAtaque * 0.01) - (otherDefesa * 0.01);

        // ==== BÔNUS por confrontos diretos ====
        long myHeadWins = headToHeadGames.stream()
                .filter(g -> {
                    boolean isHome = g.getHomeTeam().equals(myTeam);
                    boolean isAway = g.getAwayTeam().equals(myTeam);

                    int myGoals = isHome ? g.getHomeGoals() : (isAway ? g.getAwayGoals() : -1);
                    int otherGoals = isHome ? g.getAwayGoals() : (isAway ? g.getHomeGoals() : -1);

                    return myGoals > otherGoals;
                }).count();

        long otherHeadWins = headToHeadGames.stream()
                .filter(g -> {
                    boolean isHome = g.getHomeTeam().equals(otherTeam);
                    boolean isAway = g.getAwayTeam().equals(otherTeam);

                    int otherGoals = isHome ? g.getHomeGoals() : (isAway ? g.getAwayGoals() : -1);
                    int myGoals = isHome ? g.getAwayGoals() : (isAway ? g.getHomeGoals() : -1);

                    return otherGoals > myGoals;
                }).count();


        // Adiciona 0.01 por vitória direta no confronto (ajustável)
        myWinRate += myHeadWins * 0.01;
        otherWinRate += otherHeadWins * 0.01;

        // ==== Normalização final ====
        double total = myWinRate + otherWinRate;
        double myProb = total > 0 ? (myWinRate / total) * 100 : 50.0;
        double otherProb = 100.0 - myProb;

        // ==== Resultado final ====
        String probableWinner;
        String probabilityString;

        if (myProb > otherProb) {
            probableWinner = myTeam.getTeamName();
            probabilityString = probableWinner + " → " + String.format("%.2f%%", myProb);
        } else {
            probableWinner = otherTeam.getTeamName();
            probabilityString = probableWinner + " → " + String.format("%.2f%%", otherProb);
        }

        // Valor Sugerido
        BigDecimal probability = BigDecimal.valueOf(myProb).divide(BigDecimal.valueOf(100));
        BigDecimal suggestedValue = guess.getBetAmount().multiply(probability);

        setSuggestedValue(String.format("R$ %.2f", suggestedValue));

        // Provável Vencedor
        if(myWinRate > otherWinRate){
            setProbableWinner(myTeam);
            setWinProbability(probabilityString);
        }else{
            setProbableWinner(otherTeam);
            setWinProbability(probabilityString);
        }

        return probabilityString;
    }

    private void setProbableWinner(Team myTeam) {
        this.probableWinner =  myTeam.getTeamName();
    }

    public void setWinProbability(String winProbability) {
        this.winProbability = winProbability;
    }

    public String getSuggestedValue() {
        return suggestedValue;
    }

    public String getWinProbability() {
        return winProbability;
    }

    public void setSuggestedValue(String suggestedValue) {
        this.suggestedValue = suggestedValue;
    }

    public String getUnpredictability() {
        return unpredictability;
    }

    public void setUnpredictability(Team team1, Team team2) {
        double imprevisibilidade = 0.0;

        // Diferença de posição na tabela (máx: 20 posições)
        int standingDiff = Math.min(Math.abs(team1.getTableStading() - team2.getTableStading()), 25);
        imprevisibilidade += ((25 - standingDiff) / 25.0) * 25; // máx 25 pts

        // Diferença de desempenho (máx: 100 de diferença)
        int weakDiff = Math.min(Math.abs(team1.getWeakPerformance() - team2.getWeakPerformance()), 100);
        int strongDiff = Math.min(Math.abs(team1.getStrongPerformance() - team2.getStrongPerformance()), 100);
        imprevisibilidade += ((100 - weakDiff) / 100.0) * 15; // máx 15 pts
        imprevisibilidade += ((100 - strongDiff) / 100.0) * 15; // máx 15 pts

        // Diferença de competições (máx: 5 competições)
        int compDiff = Math.min(Math.abs(team1.getTotalCompetitions() - team2.getTotalCompetitions()), 5);
        imprevisibilidade += ((5 - compDiff) / 5.0) * 10; // máx 10 pts

        // Sentimentos e motivação (cada retorno entre 0 e 10, soma máx 40)
        imprevisibilidade += sentimentoParaPeso(team1.getTeamFeeling());
        imprevisibilidade += sentimentoParaPeso(team2.getTeamFeeling());
        imprevisibilidade += sentimentoParaPeso(team1.getMotivation());
        imprevisibilidade += sentimentoParaPeso(team2.getMotivation());

        double percent = Math.min(imprevisibilidade, 100.0);

        // Classificação
        String nivel;
        if (percent > 70) {
            nivel = "Alta ~" + String.format("%.1f", percent) + "%";
        } else if (percent > 30) {
            nivel = "Média ~" + String.format("%.1f", percent) + "%";
        } else if (percent > 5) {
            nivel = "Baixa ~" + String.format("%.1f", percent) + "%";
        }else{
            nivel = "Certeza de Green! ~" + String.format("%.1f", percent) + "%";
        }

        this.unpredictability = nivel;
    }

    // Transforma sentimento em peso de imprevisibilidade
    private int sentimentoParaPeso(String sentimento) {
        switch (sentimento.toUpperCase()) {
            case "RUIM": return 10;
            case "BOA": return 0;
            case "OK": default: return 3;
        }
    }


    public void setGuessId(Integer guessId) {
        this.guessId = guessId;
    }
}

