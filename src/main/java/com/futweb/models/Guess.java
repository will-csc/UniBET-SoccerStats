package com.futweb.models;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "guesses")
public class Guess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_guess")
    private Integer guessId;

    @ManyToOne
    @JoinColumn(name = "id_game", nullable = false)
    private Game gameId;

    @ManyToOne
    @JoinColumn(name = "myteam_id", nullable = false)
    private Team myTeamId;

    @ManyToOne
    @JoinColumn(name = "otherteam_id", nullable = false)
    private Team otherTeamId;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Team winnerId;

    @Column(name = "odd", precision = 10, scale = 2, nullable = false)
    private BigDecimal odd;

    @Column(name = "bet_amount", precision = 10, scale = 2)
    private BigDecimal betAmount = BigDecimal.ZERO;

    @Column(name = "profit_loss", precision = 10, scale = 2)
    private BigDecimal profitLoss;

    public Integer getGuessId() {
        return guessId;
    }

    public void setGuessId(Integer guessId) {
        this.guessId = guessId;
    }

    public Game getGameId() {
        return gameId;
    }

    public void setGameId(Game gameId) {
        this.gameId = gameId;
    }

    public Team getMyTeamId() {
        return myTeamId;
    }

    public void setMyTeamId(Team myTeamId) {
        this.myTeamId = myTeamId;
    }

    public Team getOtherTeamId() {
        return otherTeamId;
    }

    public void setOtherTeamId(Team otherTeamId) {
        this.otherTeamId = otherTeamId;
    }

    public Team getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Team winner) {
        this.winnerId = winner;
    }

    public BigDecimal getOdd() {
        return odd;
    }

    public void setOdd(BigDecimal odd) {
        this.odd = odd;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }
}
