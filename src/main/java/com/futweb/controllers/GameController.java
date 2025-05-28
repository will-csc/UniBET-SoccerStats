package com.futweb.controllers;

import com.futweb.models.Game;
import com.futweb.models.GameDTO;
import com.futweb.models.Team;
import com.futweb.services.GameService;
import com.futweb.services.TeamService;
import com.futweb.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class GameController {

    @Autowired
    public GameService gameService;

    @Autowired
    public UserService userService;

    @Autowired
    private TeamService teamService;

    @GetMapping("/games")
    public String showAllGames(HttpSession session){
        Object userId = session.getAttribute("userId");
        Integer rank = (Integer) session.getAttribute("userRank");

        if(rank < 3) {
            session.setAttribute("alerta", "Você não tem permissão para acessar esta página.");
            return "redirect:/home";
        }
        if (userId != null && userService.verifySession((Integer) userId)){
            return "games";
        }

        return "login";
    }

    @PostMapping("/api/game")
    @ResponseBody
    public ResponseEntity<?> saveGame(
            @RequestParam("game_date") String gameDate,
            @RequestParam("home_team") String homeTeamName,
            @RequestParam("home_goals") Integer homeGoals,
            @RequestParam("away_team") String awayTeamName,
            @RequestParam("away_goals") Integer awayGoals,
            @RequestParam(value = "home_corners", required = false) Integer homeCorners,
            @RequestParam(value = "away_corners", required = false) Integer awayCorners,
            @RequestParam(value = "home_shots", required = false) Integer homeShots,
            @RequestParam(value = "away_shots", required = false) Integer awayShots,
            @RequestParam(value = "game_importance", required = false) String gameImportance
    ) {
        // ---------- Verificação se os dados não estão errados ------------
        if (gameDate == null || gameDate.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("A data do jogo é obrigatória.");
        }

        if (homeTeamName == null || homeTeamName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("O time mandante é obrigatório.");
        }

        if (awayTeamName == null || awayTeamName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("O time visitante é obrigatório.");
        }

        if (homeGoals == null || homeGoals < 0) {
            return ResponseEntity.badRequest().body("Gols do mandante devem ser 0 ou mais.");
        }

        if (awayGoals == null || awayGoals < 0) {
            return ResponseEntity.badRequest().body("Gols do visitante devem ser 0 ou mais.");
        }

        // Verifica a data
        LocalDate dateParsed;
        try {
            dateParsed = LocalDate.parse(gameDate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Formato de data inválido.");
        }

        // Verifica os times
        Optional<Team> optHomeTeam = teamService.findByTeamName(homeTeamName);
        Optional<Team> optAwayTeam = teamService.findByTeamName(awayTeamName);

        if (optHomeTeam.isEmpty() || optAwayTeam.isEmpty()) {
            return ResponseEntity.badRequest().body("O time informado não existe.");
        }

        Team homeTeam = optHomeTeam.get();
        Team awayTeam = optAwayTeam.get();
        if(homeTeam.getTeamId() == awayTeam.getTeamId()){
            return ResponseEntity.badRequest().body("Os times são iguais.");
        }
        // Verificação dos jogos
        Optional<Game> existingGameOpt = gameService.findByGameDateAndHomeTeam(dateParsed, homeTeam);
        Optional<Game> existingOtherDateGameOpt = gameService.findByGameDateAndAwayTeam(dateParsed, homeTeam);

        // ---------- Verificação se é para atualizar ou salvar novo ------------
        if (existingGameOpt.isEmpty() && existingOtherDateGameOpt.isEmpty()) {
            Game game = new Game();
            game.setGameDate(dateParsed);
            game.setHomeTeam(homeTeam);
            game.setAwayTeam(awayTeam);
            game.setHomeGoals(homeGoals);
            game.setAwayGoals(awayGoals);
            game.setHomeCorners(homeCorners);
            game.setAwayCorners(awayCorners);
            game.setHomeShots(homeShots);
            game.setAwayShots(awayShots);
            game.setGameImportance(gameImportance);

            gameService.save(game);
            return ResponseEntity.ok(game);

        } else if (existingOtherDateGameOpt.isEmpty()) {
            Game game = existingGameOpt.get();
            gameService.partialSave(
                    game,
                    homeGoals,
                    awayGoals,
                    homeCorners,
                    awayCorners,
                    homeShots,
                    awayShots,
                    gameImportance
            );
            return ResponseEntity.ok("Partial");

        } else {
            return ResponseEntity.badRequest().body("Os times já têm jogos marcados nesse dia.");
        }
    }

    @GetMapping("/api/games")
    @ResponseBody
    public List<GameDTO> getAllGames() {
        List<Game> games = gameService.findAll();
        return games.stream()
                .map(game -> new GameDTO(game))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/api/game/{gameDate}/{homeTeamName}")
    @ResponseBody
    public ResponseEntity<String> deleteGame(
            @PathVariable("gameDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("homeTeamName") String homeTeamName) {

        Optional<Team> optHomeTeam = teamService.findByTeamName(homeTeamName);
        if (optHomeTeam.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Time mandante não encontrado.");
        }
        Team homeTeam = optHomeTeam.get();

        Optional<Game> existingGameOpt = gameService.findByGameDateAndHomeTeam(date, homeTeam);

        if (existingGameOpt.isPresent()) {
            Game game = existingGameOpt.get();
            gameService.deleteByGameId(game.getGameId());
            return ResponseEntity.ok("Jogo deletado com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jogo não encontrado.");
        }
    }

    @GetMapping("/api/month-games")
    @ResponseBody
    public List<GameDTO> getMonthGames() {
        return gameService.findMonthGames();
    }

}
