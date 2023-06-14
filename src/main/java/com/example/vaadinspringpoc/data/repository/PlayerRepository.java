package com.example.vaadinspringpoc.data.repository;

import com.example.vaadinspringpoc.data.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("""
            select p from Player p
            where lower(p.firstName) like lower(concat('%', :searchTerm, '%'))
            or lower(p.lastName) like lower(concat('%', :searchTerm, '%'))
            order by lastName
            """)
    List<Player> search(@Param("searchTerm") String searchTerm);

    @Modifying
    @Query("""
            update Player p
            set p.currentRank = p.currentRank - 1
            where p.currentRank > :deletedRank
            """)
    void shiftRanksDown(@Param("deletedRank") Integer deletedRank);

    /**
     * Not returning a list because if the data is in a valid state, there should only be one player at any given rank.
     */
    Optional<Player> findByCurrentRank(Integer currentRank);
}
