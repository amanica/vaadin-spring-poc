package com.example.vaadinspringpoc.data.service;

import com.example.vaadinspringpoc.data.entity.Game;
import com.example.vaadinspringpoc.data.entity.GameResult;
import com.example.vaadinspringpoc.data.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionSystemException;

import java.util.List;

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

    Player player1;
    Player player2;
    Player player5;

    @BeforeEach
    void setUp() {
        List<Player> origPlayers = playerService.findAllPlayersOrderByRank();
        assumeThat(origPlayers, hasSize(greaterThanOrEqualTo(6)));
        player1 = origPlayers.get(1);
        player2 = origPlayers.get(2);
        player5 = origPlayers.get(5);
    }

    @Test
    void findAllGamesWithFilter() {
    }

    @Test
    void findAllGamesOrderByDateDesc() {
    }

    @Test
    void countGames() {
    }

    @Test
    void saveGame_givenNewGameWithSamePlayer() {
        //given
        Game game = new Game();
        game.setWhitePlayer(player1);
        game.setBlackPlayer(player1);

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
        game.setWhitePlayer(player1);
        game.setBlackPlayer(player2);
//        game.setResult(GameResult.DRAW);

        //when
        TransactionSystemException thrown = assertThrows(TransactionSystemException.class, () -> {
            gameService.saveGame(game);
        });

        //then
        assertThat(thrown.getMessage(), containsString("must not be null"));
        assertThat(thrown.getMessage(), containsString("propertyPath=result"));
    }

    @Test
    void saveGame_givenNewGame_withLowerRankWinning_thenRanksDontChange() {
        //given
        Game game = new Game();
        game.setWhitePlayer(player1);
        game.setBlackPlayer(player5);
        game.setResult(GameResult.BLACK_WIN);
        long origGameCount = gameService.countGames();

        //when
        gameService.saveGame(game);

        //then
        assertThat(gameService.countGames(), equalTo(origGameCount + 1));
        Game newGame = gameService.findAllGamesOrderByDateDesc().get(0);
        assertThat(newGame.getWhitePlayer(), equalTo(player1));
        assertThat(newGame.getBlackPlayer(), equalTo(player5));
        assertThat(newGame.getWhiteStartRank(), equalTo(player1.getCurrentRank()));
        assertThat(newGame.getBlackStartRank(), equalTo(player5.getCurrentRank()));

        //assert ranks didn't change
        assertThat(getNewRank(player1), equalTo(player1.getCurrentRank()));
        assertThat(getNewRank(player5), equalTo(player5.getCurrentRank()));
    }

    private Integer getNewRank(Player player) {
        return playerService.getById(player.getId()).getCurrentRank();
    }
}