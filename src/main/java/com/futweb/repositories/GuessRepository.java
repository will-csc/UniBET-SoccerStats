package com.futweb.repositories;

import com.futweb.models.Guess;
import com.futweb.models.UserPerformanceDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuessRepository extends JpaRepository<Guess, Integer> {
    List<Guess> findTop10ByUserId_UserIdOrderByBetAmountDesc(Integer userId);

    @Query("""
        SELECT new com.futweb.models.UserPerformanceDTO(
            g.userId.id, 
            SUM(CASE WHEN g.winnerId IS NOT NULL AND g.winnerId.id = g.myTeamId.id THEN 1 ELSE 0 END),
            SUM(CASE WHEN g.winnerId IS NOT NULL AND g.winnerId.id != g.myTeamId.id THEN 1 ELSE 0 END)
        )
        FROM Guess g
        WHERE g.userId.id = :userId
        GROUP BY g.userId.id
    """)
    Optional<UserPerformanceDTO> findPerformanceByUserId(@Param("userId") Integer userId);

    List<Guess> findTop10ByUserIdUserIdOrderByBetAmountDesc(Integer userId);
}
