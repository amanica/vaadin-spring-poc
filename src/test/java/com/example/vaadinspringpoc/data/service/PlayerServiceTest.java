package com.example.vaadinspringpoc.data.service;

import com.example.vaadinspringpoc.data.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.junit.MatcherAssume.assumeThat;

/**
 * Makes assumptions on the demo data that is loaded
 */
@SpringBootTest
class PlayerServiceTest {
    @Autowired
    PlayerService playerService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void findAllPlayersOrderByLastname_givenBlankFilter() {
        // given
        var filter = "";
        int playerCount = (int) playerService.countPlayers();
        assumeThat(playerCount, greaterThanOrEqualTo(6));

        // when
        List<Player> allPlayersOrderByLastname = playerService.findAllPlayersOrderByLastname(filter);

        // then
        assertThat(allPlayersOrderByLastname, hasSize(playerCount));
        assertOrderByLastName(allPlayersOrderByLastname);
    }

    @Test
    void findAllPlayersOrderByLastname_givenNonBlankFilter() {
        // given
        var filter = "an";

        // when
        List<Player> filteredPlayersOrderByLastname = playerService.findAllPlayersOrderByLastname(filter);

        // then
        assertThat(filteredPlayersOrderByLastname, hasSize(3));
        assertFirstNameContains(filteredPlayersOrderByLastname, filter);
        assertOrderByLastName(filteredPlayersOrderByLastname);
    }

    private void assertFirstNameContains(List<Player> players, String needle) {
        for (Player player : players) {
            assertThat(player.getFirstName(), containsString(needle));
        }
        for (int i = 0; i < players.size() -1; i++) {
            assertThat(players.get(i).getLastName(),
                    lessThan(players.get(i + 1).getLastName()));
        }
    }

    private void assertOrderByLastName(List<Player> players) {
        for (int i = 0; i < players.size() -1; i++) {
            assertThat(players.get(i).getLastName(),
                lessThan(players.get(i + 1).getLastName()));
        }
    }

    @Test
    void deletePlayer() {
        //TODO
    }

    @Test
    void savePlayer() {
        //TODO
    }
}