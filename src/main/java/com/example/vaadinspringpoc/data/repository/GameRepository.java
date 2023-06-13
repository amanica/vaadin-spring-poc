package com.example.vaadinspringpoc.data.repository;

import com.example.vaadinspringpoc.data.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("""
            select g from Game g
            where lower(g.whitePlayer.firstName) like lower(concat('%', :searchTerm, '%'))
               or lower(g.whitePlayer.lastName) like lower(concat('%', :searchTerm, '%'))
               or lower(g.blackPlayer.firstName) like lower(concat('%', :searchTerm, '%'))
               or lower(g.blackPlayer.lastName) like lower(concat('%', :searchTerm, '%'))
            order by dateTime DESC
            """)
    List<Game> search(@Param("searchTerm") String searchTerm);
}
