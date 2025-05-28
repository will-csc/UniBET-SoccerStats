package com.futweb.controllers;

import com.futweb.models.*;
import com.futweb.records.ParsedGame;
import com.futweb.services.GameService;
import com.futweb.services.GuessService;
import com.futweb.services.TeamService;
import com.futweb.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GuessController {

    @Autowired
    public GuessService guessService;

    @Autowired
    public TeamService teamService;

    @Autowired
    public GameService gameService;

    @Autowired
    public UserService userService;

    @GetMapping("/bets")
    public String showBets(HttpSession session){
        Object userId = session.getAttribute("userId");
        if (userId != null && userService.verifySession((Integer) userId)){
            return "guesses";
        }
        return "login";
    }

    @PostMapping("/new-guess")
    public ResponseEntity<String>  insertNewGuess(
            @RequestParam("game") String gameString,
            @RequestParam("winner_id") String winner_team,
            @RequestParam("betAmount") String betAmountStr,
            @RequestParam("odd") BigDecimal odd,
            HttpSession session) {

        // Recupera o userId da sessão
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
        Optional<User> userOpt = userService.findUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado.");
        }
        User user = userOpt.get();

        // Valor
        BigDecimal betAmount = gameService.betAmount(betAmountStr);

        // Times e Data
        ParsedGame parsed = gameService.parseGameString(gameString,winner_team);
        String loserTeam = parsed.teamB();
        LocalDate date = parsed.gameDate();

        // Calcula lucro/prejuízo estimado
        BigDecimal profit = betAmount.multiply(odd);

        // Times Apostados
        Optional<Team> myTeamOpt = teamService.findByTeamName(winner_team);
        Optional<Team> otherTeamOpt = teamService.findByTeamName(loserTeam);

        if (myTeamOpt.isEmpty() || otherTeamOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Os times informados não existem.");
        }

        Team myTeam = myTeamOpt.get();
        Team otherTeam = otherTeamOpt.get();

        Optional<Game> gameOpt = gameService.findByGameDayAndTeams(date, myTeam.getTeamId(), otherTeam.getTeamId());

        if (gameOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Jogo não encontrado com os times e a data fornecidos.");
        }

        // Cria a aposta e salva ela
        Guess guess = new Guess();

        guess.setUserId(user);
        guess.setGameId(gameOpt.get());
        guess.setMyTeamId(myTeam);
        guess.setOtherTeamId(otherTeam);
        guess.setOdd(odd);
        guess.setBetAmount(betAmount);
        guess.setProfitLoss(profit); // valor estimado até o jogo acontecer

        // Salva aposta
        guessService.save(guess);

        // Retorna status 200 com mensagem de sucesso
        return ResponseEntity.ok("Aposta salva com sucesso!");
    }

    @GetMapping("/api/bets")
    @ResponseBody
    public List<GuessDTO> getAllGuesses(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        List<Guess> guesses = guessService.findAll(userId);

        return guesses.stream().map(guess -> {
            GuessDTO dto = new GuessDTO();

            Team myTeam = guess.getMyTeamId();
            Game game = guess.getGameId();
            String gameDate = game.getGameDate().toString();

            Team homeTeam = game.getHomeTeam();
            Team awayTeam = game.getAwayTeam();

            // Buscar últimos 10 jogos de cada time
            List<Game> homeRecentGames = gameService.findTop10ByHomeTeamOrAwayTeamOrderByGameDateDesc(homeTeam, homeTeam);
            List<Game> awayRecentGames = gameService.findTop10ByHomeTeamOrAwayTeamOrderByGameDateDesc(awayTeam, awayTeam);
            List<Game> directGames = gameService.findTop5ByTeamsOrderByGameDateDesc(awayTeam, homeTeam);

            // Combina os jogos dos dois times, sem duplicar
            Set<Game> historicoJogos = new HashSet<>();
            historicoJogos.addAll(homeRecentGames);
            historicoJogos.addAll(awayRecentGames);
            historicoJogos.addAll(directGames);

            // Preenche DTO
            dto.setGuessId(guess.getGuessId());
            dto.setWinner(myTeam.getTeamName());
            dto.setOdd(guess.getOdd());
            dto.setValue(guess.getBetAmount());
            dto.setGameDate(gameDate);
            dto.setProbableWinner(
                    homeTeam,
                    awayTeam,
                    game,
                    new ArrayList<>(historicoJogos), // Aqui vai para setProbableWinner
                    guess
            );
            dto.setUnpredictability(homeTeam, awayTeam);

            return dto;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @DeleteMapping("api/delete-bet/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteGame(@PathVariable Integer id) {
        try {
            // Verifica se o registro existe
            if (!guessService.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aposta não encontrada.");
            }
            // Deleta a aposta
            guessService.deleteById(id);
            return ResponseEntity.ok("Aposta deletada com sucesso.");
        } catch (Exception e) {
            // Em caso de erro, retorna 500 com a mensagem do erro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar aposta: " + e.getMessage());
        }
    }

    @PutMapping("/api/update-bet/{id}")
    public ResponseEntity<?> updateBetStatus(@PathVariable Integer id, @RequestParam("option") int option) {
        Optional<Guess> guessOpt = guessService.findById(id);
        if (guessOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Guess guess = guessOpt.get();
        Game game = guess.getGameId();
        Team myTeam = guess.getMyTeamId();
        Team otherTeam = guess.getOtherTeamId();

        Team homeTeam = game.getHomeTeam();

        int homeGoals = game.getHomeGoals();
        int awayGoals = game.getAwayGoals();

        Team winnerTeam;
        Team loserTeam;

        if (option == 1) {
            winnerTeam = myTeam;
            loserTeam = otherTeam;
        } else {
            winnerTeam = otherTeam;
            loserTeam = myTeam;
        }

        if (winnerTeam.getTeamId().equals(homeTeam.getTeamId())) {
            winnerTeam.setGoals_scored(homeGoals);
            winnerTeam.setGoals_conceded(awayGoals);
            loserTeam.setGoals_scored(awayGoals);
            loserTeam.setGoals_conceded(homeGoals);
        } else {
            winnerTeam.setGoals_scored(awayGoals);
            winnerTeam.setGoals_conceded(homeGoals);
            loserTeam.setGoals_scored(homeGoals);
            loserTeam.setGoals_conceded(awayGoals);
        }

        if (winnerTeam.getGoals_conceded() < 0) {
            winnerTeam.setGoals_conceded(0);
        }
        if (loserTeam.getGoals_conceded() < 0) {
            loserTeam.setGoals_conceded(0);
        }

        guess.setWinnerId(winnerTeam);

        // Atualizar Dados do vencedor com base na posição do perdedor
        if (loserTeam.getTableStading() > 10) {
            winnerTeam.setStrongPerformance(true);
        } else {
            winnerTeam.setWeakPerformance(true);
        }

        if (winnerTeam.getTableStading() > 10) {
            loserTeam.setStrongPerformance(false);
        } else {
            loserTeam.setWeakPerformance(false);
        }

        // Atualizar teamFeeling diretamente com base no desempenho
        if (loserTeam.getTableStading() > 10) {
            winnerTeam.setTeamFeeling("BOA");
        } else {
            winnerTeam.setTeamFeeling("RUIM");
        }

        // Atualizar motivação de forma progressiva
        String currentMotivation = winnerTeam.getMotivation();
        switch (currentMotivation) {
            case "BOA":
                winnerTeam.setMotivation("OK");
                break;
            case "OK":
                winnerTeam.setMotivation("RUIM");
                break;
            case "RUIM":
            default:
                winnerTeam.setMotivation("RUIM"); // já está ruim, não muda
                break;
        }

        currentMotivation = loserTeam.getMotivation();
        switch (currentMotivation) {
            case "BOA":
                loserTeam.setMotivation("OK");
                break;
            case "OK":
                loserTeam.setMotivation("RUIM");
                break;
            case "RUIM":
            default:
                loserTeam.setMotivation("RUIM"); // já está ruim, não muda
                break;
        }

        // Salva tudo (talvez seja necessário salvar Game e os Times também, depende do seu repositório)
        guessService.save(guess);
        gameService.save(game);      // se houver
        teamService.save(winnerTeam);
        teamService.save(loserTeam);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/top-bets")
    @ResponseBody
    public List<GuessDTO> getTopGuesses(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        List<Guess> topGuesses = guessService.getTopBets(userId);

        return topGuesses.stream().map(guess -> {
            GuessDTO dto = new GuessDTO();

            Team myTeam = guess.getMyTeamId();
            Team otherTeam = guess.getOtherTeamId();

            // Preenche DTO
            dto.setGuessId(guess.getGuessId());
            dto.setOtherTeam(otherTeam.getTeamName());
            dto.setMyTeam(myTeam.getTeamName());
            dto.setWinnerImage(myTeam.getTeam_image());
            dto.setOdd(guess.getOdd());
            dto.setValue(guess.getBetAmount());
            dto.setWinnerId(guess.getWinnerId().getTeamId());
            dto.setMyTeamId(myTeam.getTeamId());

            return dto;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
