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

    /**
     * Decrease rank by one for every play higher than :exclusiveLowerBound
     */
    @Modifying
    @Query("""
            update Player p
            set p.currentRank = p.currentRank - 1
            where p.currentRank > :exclusiveLowerBound
            """)
    void decrementRanks(@Param("exclusiveLowerBound") Integer exclusiveLowerBound);

    /**
     * To move a player up, we have to increase the rank for every player in between by 1
     * except the player being moved up which gets promoted to the start.
     * <p>
     * Ex. If we want to hypothetically move player with rank b=2 to rank a=0 in set [0 1 2]
     * we want to see this:
     * 0 -> 1
     * 1 -> 2
     * 2 -> 0
     *
     * So if we add 1 to each value and just do a modulus operation based of the length we can achieve that:
     * Where the length of the range is (b - a + 1)
     * f(x) = (x + 1) % (b - a + 1)
     *      = (x + 1) % (2 - 0 + 1)
     *      = (x + 1) % 3
     * So we get this:
     * f(0) = (0 + 1) % 3 = 1 % 3 = 1
     * f(1) = (1 + 1) % 3 = 2 % 3 = 2
     * f(2) = (2 + 1) % 3 = 3 % 3 = 0
     * <p>
     *
     * The above assumes a=0, but if we want to for example move b=4 to a=2 in set [2 3 4], then we want:
     * 2 -> 3
     * 3 -> 4
     * 4 -> 2
     * So we need to subtract (a) first and then add (a) back at end:
     * f(x) = (x - (a) + 1) % (b - a + 1) + (a)
     *      = (x - 2 + 1) % (4 - 2 + 1) + 2
     *      = (x - 1) % 3 + 2
     * f(2) = (2 - 1) % 3 + 2 = 1 % 3 + 2 = 1 + 2 = 3
     * f(3) = (3 - 1) % 3 + 2 = 2 % 3 + 2 = 2 + 2 = 4
     * f(4) = (4 - 1) % 3 + 2 = 3 % 3 + 2 = 0 + 2 = 2
     */
    @Modifying
    @Query("""
            update Player p
            set p.currentRank = MOD(p.currentRank - :newRank + 1, :origRank - :newRank + 1) + :newRank
            where p.currentRank BETWEEN :newRank AND :origRank
            """)
    void promotePlayer(@Param("origRank") Integer origRank, @Param("newRank") Integer newRank);

    /**
     * Not returning a list because if the data is in a valid state, there should only be one player at any given rank.
     */
    Optional<Player> findByCurrentRank(Integer currentRank);
}
