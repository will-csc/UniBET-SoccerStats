package com.futweb.repositories;

import com.futweb.models.Team;
import com.futweb.models.TeamDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    Optional<Team> findByTeamName(String name);

    @Query("""
        SELECT new com.futweb.models.TeamDTO(
            t.teamId,
            t.teamName,
            t.team_image,
            SUM(CASE WHEN g.winnerId.teamId = t.teamId THEN 1 ELSE 0 END),
            SUM(CASE WHEN g.winnerId IS NOT NULL AND g.winnerId.teamId <> t.teamId THEN 1 ELSE 0 END)
        )
        FROM Team t
        LEFT JOIN Guess g ON g.myTeamId.teamId = t.teamId OR g.otherTeamId.teamId = t.teamId
        GROUP BY t.teamId, t.teamName, t.team_image
    """)
    List<TeamDTO> findTeamsWithMostGreens();



    Team findByTeamId(Integer teamId);
}
