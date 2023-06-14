package com.example.vaadinspringpoc.data.service;

import com.example.vaadinspringpoc.data.entity.Game;
import com.example.vaadinspringpoc.data.entity.GameResult;
import com.example.vaadinspringpoc.data.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.junit.MatcherAssume.assumeThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Makes assumptions on the demo data that is loaded
 */
@SpringBootTest
class GameServiceTest {

    @Autowired
    GameService gameService;
    @Autowired
    PlayerService playerService;
    
    List<Player> origPlayers;
    List<Integer> origRanks;

    @BeforeEach
    void setUp() {
        origPlayers = playerService.findAllPlayersOrderByRank();
        assumeThat(origPlayers, hasSize(greaterThanOrEqualTo(6)));
        origRanks = origPlayers.stream().map(Player::getCurrentRank).collect(Collectors.toList());
    }

    @Test
    void saveGame_givenNewGameWithSamePlayer() {
        //given
        Game game = new Game();
        game.setWhitePlayer(origPlayers.get(1));
        game.setBlackPlayer(origPlayers.get(1));

        //when
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            gameService.saveGame(game);
        });

        //then
        assertThat(thrown.getMessage(), containsString("Game with the same player for white and black is not valid"));
    }

    @Test
    void saveGame_givenNewGame_withNullResult() {
        //given
        Game game = new Game();
        game.setWhitePlayer(origPlayers.get(1));
        game.setBlackPlayer(origPlayers.get(2));
//        game.setResult(GameResult.DRAW);

        //when
        NullPointerException thrown = assertThrows(NullPointerException.class, () -> {
            gameService.saveGame(game);
        });

        //then exception was thrown
    }

    @Test
    void saveGame_givenNewGame_withLowerRankWinning_thenRanksDontChange() {
        //given
        Game game = new Game();
        game.setWhitePlayer(origPlayers.get(1));
        game.setBlackPlayer(origPlayers.get(5));
        game.setResult(GameResult.BLACK_WIN);
        long origGameCount = gameService.countGames();

        //when
        gameService.saveGame(game);

        //then the came persisted and retrieved correctly
        assertThat(gameService.countGames(), equalTo(origGameCount + 1));
        Game newGame = gameService.findAllGamesOrderByDateDesc().get(0);
        assertThat(newGame.getWhitePlayer(), equalTo(origPlayers.get(1)));
        assertThat(newGame.getBlackPlayer(), equalTo(origPlayers.get(5)));
        assertThat(newGame.getWhiteStartRank(), equalTo(origRanks.get(1)));
        assertThat(newGame.getBlackStartRank(), equalTo(origRanks.get(5)));

        //and ranks didn't change
        assertThat(getNewRank(origPlayers.get(1)), equalTo(origRanks.get(1)));
        assertThat(getNewRank(origPlayers.get(5)), equalTo(origRanks.get(5)));
    }

    @Test
    void saveGame_givenNewGame_withAdjacentDraw_thenRanksDontChange() {
        //given
        Game game = new Game();
        game.setWhitePlayer(origPlayers.get(1));
        game.setBlackPlayer(origPlayers.get(2));
        game.setResult(GameResult.DRAW);

        //when
        gameService.saveGame(game);

        //then ranks didn't change
        assertThat(getNewRank(origPlayers.get(1)), equalTo(origRanks.get(1)));
        assertThat(getNewRank(origPlayers.get(2)), equalTo(origRanks.get(2)));
    }

    @Test
    void saveGame_givenNewGame_withDraw_thenBlackLowerRankMovesUpOne() {
        //given
        Game game = new Game();
        game.setWhitePlayer(origPlayers.get(1));
        game.setBlackPlayer(origPlayers.get(5)); // black has lower rank
        game.setResult(GameResult.DRAW);

        //when
        gameService.saveGame(game);

        //then white rank didn't change
        assertThat(getNewRank(origPlayers.get(1)), equalTo(origRanks.get(1)));
        //and player above black moves down one
        assertThat(getNewRank(origPlayers.get(4)), equalTo(origRanks.get(4) + 1));
        //and black move up one
        assertThat(getNewRank(origPlayers.get(5)), equalTo(origRanks.get(5) - 1));
    }

    @Test
    void saveGame_givenNewGame_withDraw_thenWhiteLowerRankMovesUpOne() {
        //given
        Game game = new Game();
        game.setWhitePlayer(origPlayers.get(5)); // white has lower rank
        game.setBlackPlayer(origPlayers.get(1));
        game.setResult(GameResult.DRAW);

        //when
        gameService.saveGame(game);

        //then black rank didn't change
        assertThat(getNewRank(origPlayers.get(1)), equalTo(origRanks.get(1)));
        //and player above white moves down one
        assertThat(getNewRank(origPlayers.get(4)), equalTo(origRanks.get(4) + 1));
        //and white move up one
        assertThat(getNewRank(origPlayers.get(5)), equalTo(origRanks.get(5) - 1));
    }

    private Integer getNewRank(Player player) {
        return playerService.getById(player.getId()).getCurrentRank();
    }
}