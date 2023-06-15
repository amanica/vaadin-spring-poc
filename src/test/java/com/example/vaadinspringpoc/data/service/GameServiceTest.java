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
    void saveGame_givenNewGame_withHigherRankWinning_thenRanksDontChange() {
        //given
        Game game = new Game();
        game.setWhitePlayer(origPlayers.get(1));
        game.setBlackPlayer(origPlayers.get(5));
        game.setResult(GameResult.WHITE_WIN);
        long origGameCount = gameService.countGames();
        int whiteOrigGamesPlayed = origPlayers.get(1).getGamesPlayed();
        int blackOrigGamesPlayed = origPlayers.get(5).getGamesPlayed();

        //when
        gameService.saveGame(game);

        //then the game persisted and retrieved correctly
        assertThat(gameService.countGames(), equalTo(origGameCount + 1));
        Game newGame = gameService.findAllGamesOrderByDateDesc().get(0);
        assertThat(newGame.getWhitePlayer(), equalTo(origPlayers.get(1)));
        assertThat(newGame.getBlackPlayer(), equalTo(origPlayers.get(5)));
        assertThat(newGame.getWhiteStartRank(), equalTo(origRanks.get(1)));
        assertThat(newGame.getBlackStartRank(), equalTo(origRanks.get(5)));
        assertThat(getNewGamesPlayed(origPlayers.get(1)), equalTo(whiteOrigGamesPlayed + 1));
        assertThat(getNewGamesPlayed(origPlayers.get(5)), equalTo(blackOrigGamesPlayed + 1));

        //and ranks didn't change
        assertThat(getNewRank(origPlayers.get(1)), equalTo(origRanks.get(1)));
        assertThat(getNewRank(origPlayers.get(5)), equalTo(origRanks.get(5)));
    }

    @Test
    void saveGame_givenNewGame_withLowerRankWinning_adjacent() {
        //given
        Game game = new Game();
        game.setWhitePlayer(origPlayers.get(1));
        game.setBlackPlayer(origPlayers.get(2));
        game.setResult(GameResult.BLACK_WIN);
        long origGameCount = gameService.countGames();

        //when
        gameService.saveGame(game);

        //then loser + 1 and winner - 1
        assertThat(getNewRank(origPlayers.get(1)), equalTo(origRanks.get(1) + 1));
        assertThat(getNewRank(origPlayers.get(2)), equalTo(origRanks.get(2) - 1));
    }

    @Test
    void saveGame_givenNewGame_withLowerRankWinning_1appart() {
        //given
        Game game = new Game();
        game.setWhitePlayer(origPlayers.get(1));
        game.setBlackPlayer(origPlayers.get(3));
        game.setResult(GameResult.BLACK_WIN);
        long origGameCount = gameService.countGames();

        //when
        gameService.saveGame(game);

        //then loser + 1 and winner - 1
        assertThat(getNewRank(origPlayers.get(1)), equalTo(origRanks.get(1) + 2));
        assertThat(getNewRank(origPlayers.get(3)), equalTo(origRanks.get(3) - 1));
    }

    @Test
    void saveGame_givenNewGame_withLowerRankWinning_6appart() {
        //given
        Game game = new Game();
        Player higherRankedPlayer = findPlayerWithOrigRank(10);
        Player lowerRankedPlayer = findPlayerWithOrigRank(16);
        game.setWhitePlayer(higherRankedPlayer);
        game.setBlackPlayer(lowerRankedPlayer);
        game.setResult(GameResult.BLACK_WIN);
        long origGameCount = gameService.countGames();

        //when
        gameService.saveGame(game);

        //then higherRankedPlayer=loser + 1 and lowerRankedPlayer=winner -> (16-10)/2=3
        assertThat(getNewRank(higherRankedPlayer), equalTo(10 + 1));
        assertThat(getNewRank(lowerRankedPlayer), equalTo(16 - 3));
        // and other players inbetween move down 1 each
        for (int i = 13; i < 15; i++) {
            assertThat(getNewRank(findPlayerWithOrigRank(i)), equalTo(i + 1));
        }
    }

    /**
     * test odd difference to check what happens with rounding
     */
    @Test
    void saveGame_givenNewGame_withLowerRankWinning_5appart() {
        //given
        Game game = new Game();
        Player higherRankedPlayer = findPlayerWithOrigRank(10);
        Player lowerRankedPlayer = findPlayerWithOrigRank(15);
        game.setWhitePlayer(higherRankedPlayer);
        game.setBlackPlayer(lowerRankedPlayer);
        game.setResult(GameResult.BLACK_WIN);
        long origGameCount = gameService.countGames();

        //when
        gameService.saveGame(game);

        //then higherRankedPlayer=loser + 1 and lowerRankedPlayer=winner -> (15-10)/2=2
        assertThat(getNewRank(higherRankedPlayer), equalTo(10 + 1));
        assertThat(getNewRank(lowerRankedPlayer), equalTo(15 - 2));
        // and other players inbetween move down 1 each
        for (int i = 13; i < 14; i++) {
            assertThat(getNewRank(findPlayerWithOrigRank(i)), equalTo(i + 1));
        }
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

    private Player findPlayerWithOrigRank(int origRank) {
        return origPlayers.stream().filter(player -> player.getCurrentRank() == origRank).findFirst().orElseThrow();
    }

    private Integer getNewRank(Player player) {
        return playerService.getById(player.getId()).getCurrentRank();
    }

    private Integer getNewGamesPlayed(Player player) {
        return playerService.getById(player.getId()).getGamesPlayed();
    }
}