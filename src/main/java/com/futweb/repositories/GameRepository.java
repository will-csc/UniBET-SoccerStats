package com.futweb.repositories;

import com.futweb.models.Game;
import com.futweb.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findByGameDateAndHomeTeam(LocalDate gameDate, Team homeTeam);

    Optional<Game> findByGameDateAndAwayTeam(LocalDate gameDate, Team awayTeam);

    @Query("""
        SELECT g FROM Game g
        WHERE g.gameDate = :gameDay
          AND (
                (g.homeTeam.teamId = :team1Id AND g.awayTeam.teamId = :team2Id)
             OR (g.homeTeam.teamId = :team2Id AND g.awayTeam.teamId = :team1Id)
          )
    """)
    Optional<Game> findByGameDayAndTeams(
            @Param("gameDay") LocalDate gameDay,
            @Param("team1Id") Integer team1Id,
            @Param("team2Id") Integer team2Id
    );

    @Query("SELECT g FROM Game g WHERE (g.homeTeam = :team1 AND g.awayTeam = :team2) OR (g.homeTeam = :team2 AND g.awayTeam = :team1) ORDER BY g.gameDate DESC")
    List<Game> findTop5ByTeamsOrderByGameDateDesc(@Param("team1") Team team1, @Param("team2") Team team2, org.springframework.data.domain.Pageable pageable);

    List<Game> findTop10ByHomeTeamOrAwayTeamOrderByGameDateDesc(Team team1, Team team2);

    List<Game> findByGameDateBetween(LocalDate startOfMonth, LocalDate endOfMonth);
}
