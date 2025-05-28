package com.futweb.services;

import com.futweb.models.Game;
import com.futweb.models.Guess;
import com.futweb.models.UserPerformanceDTO;
import com.futweb.repositories.GameRepository;
import com.futweb.repositories.GuessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GuessService {

    @Autowired
    private GuessRepository guessRepository;

    @Autowired
    private GameRepository gameRepository;

    public void save(Guess guess) {
        guessRepository.save(guess);
    }

    public Optional<Game> findByGameDayAndTeams(LocalDate gameDay, Integer team1Id, Integer team2Id) {
        return gameRepository.findByGameDayAndTeams(gameDay, team1Id, team2Id);
    }

    public List<Guess> findAll(Integer userId) {
        return guessRepository.findTop10ByUserIdUserIdOrderByBetAmountDesc(userId);
    }

    public void deleteById(Integer id) {
        guessRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return guessRepository.existsById(id);
    }

    public Optional<Guess> findById(Integer id) {
        return guessRepository.findById(id);
    }

    public List<Guess> getTopBets(Integer idUser){
        return guessRepository.findTop10ByUserId_UserIdOrderByBetAmountDesc(idUser);
    }

    public Optional<UserPerformanceDTO> findPerformanceByUserId(Integer userId) {
        return guessRepository.findPerformanceByUserId(userId);
    }
}
