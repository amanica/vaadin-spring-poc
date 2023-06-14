package com.example.vaadinspringpoc.data.repository;

import com.example.vaadinspringpoc.data.entity.Game;
import com.example.vaadinspringpoc.data.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("""
            SELECT g FROM Game g
            WHERE LOWER(g.whitePlayer.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(g.whitePlayer.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(g.blackPlayer.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
               OR LOWER(g.blackPlayer.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            ORDER BY dateTime DESC
            """)
    List<Game> search(@Param("searchTerm") String searchTerm);

    @Modifying
    @Query("""
            SELECT g FROM Game g
            WHERE g.whitePlayer = :player
               OR g.blackPlayer = :player
            ORDER BY dateTime DESC
            """)
    List<Game> findGamesByPlayer(@Param("player") Player player);
}
