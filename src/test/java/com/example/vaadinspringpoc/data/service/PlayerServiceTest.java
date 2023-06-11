package com.example.vaadinspringpoc.data.service;

import com.example.vaadinspringpoc.data.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.junit.MatcherAssume.assumeThat;

/**
 * Makes assumptions on the demo data that is loaded
 */
@SpringBootTest
class PlayerServiceTest {
    public static final String DUMMY_FIRST_NAME = "DummyFirstName";
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

    @Test
    void deletePlayer() {
        // given
        List<Player> origPlayers = playerService.findAllPlayersOrderByLastname("");
        assumeThat(origPlayers, hasSize(greaterThanOrEqualTo(6)));
        Player playerToDelete = origPlayers.get(2);

        // when
        playerService.deletePlayer(playerToDelete);

        // then
        List<Player> allPlayersOrderByRank = playerService.findAllPlayersOrderByRank();
        assertThat(allPlayersOrderByRank, hasSize(origPlayers.size()-1));
        assertOrderByRankWithNoGaps(allPlayersOrderByRank);
    }

    @Test
    void savePlayer_givenANewPlayer() {
        //given
        int originalPlayerCount = (int) playerService.countPlayers();
        assumeThat(originalPlayerCount, greaterThan(0));

        Player playerToSave = new Player();
        playerToSave.setFirstName(DUMMY_FIRST_NAME);
        playerToSave.setLastName("DummyLastName");
        playerToSave.setEmail("dummy@example.com");

        //when
        playerService.savePlayer(playerToSave);

        //then
        List<Player> foundPlayers = playerService.findAllPlayersOrderByLastname(DUMMY_FIRST_NAME);
        assertThat(foundPlayers, hasSize(1));
        Player player = foundPlayers.get(0);
        assertThat(player.getFirstName(), equalTo(DUMMY_FIRST_NAME));
        assertThat("Business rule: New players will, by default, be ranked last.", player.getCurrentRank(), equalTo(originalPlayerCount + 1));
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

    /**
     * Check that each player's rank is one less than the next player,
     * also checks that each rank is in the range [1,max].
     */
    private void assertOrderByRankWithNoGaps(List<Player> players) {
        for (int i = 0; i < players.size() -1; i++) {
            Integer currentRank = players.get(i).getCurrentRank();
            assertRankInRange(currentRank, players);
            assertThat(currentRank,
                    equalTo(players.get(i + 1).getCurrentRank()-1));
        }
        // check last player rank range as the loop above skips it.
        Integer currentRank = players.get(players.size()-1).getCurrentRank();
        assertRankInRange(currentRank, players);
    }

    private void assertRankInRange(Integer currentRank, List<Player> players) {
        assertThat(currentRank, greaterThanOrEqualTo(1));
        assertThat(currentRank, lessThanOrEqualTo(players.size()));
    }
}