package com.futweb.services;

import com.futweb.models.GameDTO;
import com.futweb.records.ParsedGame;
import com.futweb.models.Game;
import com.futweb.models.Team;
import com.futweb.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public void save(Game game) {
        gameRepository.save(game);
    }

    public Optional<Game> findByGameDateAndHomeTeam(LocalDate gameDate, Team homeTeam) {
        return gameRepository.findByGameDateAndHomeTeam(gameDate,homeTeam);
    }

    public Optional<Game> findByGameDateAndAwayTeam(LocalDate gameDate, Team awayTeam) {
        return gameRepository.findByGameDateAndAwayTeam(gameDate,awayTeam);
    }

    // Atualização parcial
    public void partialSave(Game game,
                            Integer homeGoals,
                            Integer awayGoals,
                            Integer homeCorners,
                            Integer awayCorners,
                            Integer homeShots,
                            Integer awayShots,
                            String gameImportance) {

        if(homeGoals + awayGoals != game.getTotalGoals()) {
            if (homeGoals != null && homeGoals >= 0) {
                game.setHomeGoals(homeGoals);
            }
            if (awayGoals != null && awayGoals >= 0) {
                game.setAwayGoals(awayGoals);
            }
            game.setTotalGoals(homeGoals + awayGoals);
        }
        if (homeCorners != null) {
            game.setHomeCorners(homeCorners);
        }
        if (awayCorners != null) {
            game.setAwayCorners(awayCorners);
        }
        if (homeShots != null) {
            game.setHomeShots(homeShots);
        }
        if (awayShots != null) {
            game.setAwayShots(awayShots);
        }
        if (gameImportance != null && !gameImportance.trim().isEmpty()) {
            game.setGameImportance(gameImportance);
        }
        gameRepository.save(game);
    }

    public void deleteByGameId(Integer gameId) {
        gameRepository.deleteById(gameId);
    }

    public Optional<Game> findByGameDayAndTeams(LocalDate gameDay, Integer team1Id, Integer team2Id) {
        return gameRepository.findByGameDayAndTeams(gameDay, team1Id, team2Id);
    }

    public BigDecimal betAmount(String betAmountStr){
        // Converte betAmount para BigDecimal
        String cleanBet = betAmountStr.replace("R$", "").replace(".", "").replace(",", ".").trim();
        return new BigDecimal(cleanBet);
    }

    public ParsedGame parseGameString(String gameString, String winner) {
        if (gameString == null || !gameString.contains(" x ") || !gameString.contains(" - ")) {
            throw new IllegalArgumentException("Formato do jogo inválido. Esperado: 'Time A x Time B - yyyy-MM-dd'");
        }

        String[] parts = gameString.split(" - ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato do jogo inválido. Falha ao separar data.");
        }

        String[] teams = parts[0].split(" x ");
        if (teams.length != 2) {
            throw new IllegalArgumentException("Formato do jogo inválido. Falha ao separar times.");
        }

        String teamA;
        String teamB;

        if (winner.equals(teams[0])){
            teamA = teams[0].trim();
            teamB = teams[1].trim();
        }else{
            teamA = teams[1].trim();
            teamB = teams[0].trim();
        }

        LocalDate date = LocalDate.parse(parts[1].trim());

        return new ParsedGame(teamA, teamB, date);
    }

    public List<Game> findTop5ByTeamsOrderByGameDateDesc(Team team1, Team team2){
        Pageable limit = PageRequest.of(0, 5);
        return gameRepository.findTop5ByTeamsOrderByGameDateDesc(team1, team2, limit);
    };

    public List<Game> findTop10ByHomeTeamOrAwayTeamOrderByGameDateDesc(Team team1, Team team2){
        return gameRepository.findTop10ByHomeTeamOrAwayTeamOrderByGameDateDesc(team1,team2);
    }

    public List<GameDTO> findMonthGames() {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        List<Game> gamesThisMonth = gameRepository.findByGameDateBetween(startOfMonth, endOfMonth);

        return gamesThisMonth.stream()
                .map(GameDTO::new)  // usa o construtor que recebe o Game
                .collect(Collectors.toList());
    }

}
