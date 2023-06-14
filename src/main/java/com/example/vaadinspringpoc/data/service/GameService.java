package com.example.vaadinspringpoc.data.service;

import com.example.vaadinspringpoc.data.entity.Game;
import com.example.vaadinspringpoc.data.entity.GameResult;
import com.example.vaadinspringpoc.data.entity.Player;
import com.example.vaadinspringpoc.data.repository.GameRepository;
import com.example.vaadinspringpoc.data.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.example.vaadinspringpoc.data.entity.GameResult.DRAW;

@Service
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public GameService(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public List<Game> findAllGamesWithFilter(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return findAllGamesOrderByDateDesc();
        } else {
            return gameRepository.search(stringFilter);
        }
    }

    public List<Game> findAllGamesOrderByDateDesc() {
        return gameRepository.findAll(Sort.by(Sort.Direction.DESC, "dateTime"));
    }

    public List<Game> findAllGamesByPlayer() {
        return gameRepository.findAll(Sort.by(Sort.Direction.DESC, "dateTime"));
    }

    public long countGames() {
        return gameRepository.count();
    }

    @Transactional
    public void saveGame(Game game) {
        if (game == null) {
            log.error("Game is null.");
            return;
        }
        if (!game.isGameWithDifferentPlayers()) {
            throw new IllegalArgumentException("Game with the same player for white and black is not valid: "
                    + game.getWhitePlayer().getFullName());
        }
        Objects.requireNonNull(game.getResult());
        if (game.getId() == null) {
            // new game
            game.setDateTime(LocalDateTime.now());
            game.setWhiteStartRank(game.getWhitePlayer().getCurrentRank());
            game.setBlackStartRank(game.getBlackPlayer().getCurrentRank());

            updateRanks(game);
        } else {
            throw new IllegalArgumentException("Updating games are not supported at the moment.");
        }
        gameRepository.save(game);
    }

    private void updateRanks(Game game) {
        Player whitePlayer = game.getWhitePlayer();
        Player blackPlayer = game.getBlackPlayer();
        final Player higherRankedPlayer, lowerRankedPlayer;
        boolean lowerRankedPlayerWon;

        // higher rank has lower number!
        if (whitePlayer.getCurrentRank() < blackPlayer.getCurrentRank()) {
            higherRankedPlayer = whitePlayer;
            lowerRankedPlayer = blackPlayer;
            lowerRankedPlayerWon = game.getResult().equals(GameResult.BLACK_WIN);
        } else {
            higherRankedPlayer = blackPlayer;
            lowerRankedPlayer = whitePlayer;
            lowerRankedPlayerWon = game.getResult().equals(GameResult.WHITE_WIN);
        }

        int higherRank = higherRankedPlayer.getCurrentRank();
        int lowerRank = lowerRankedPlayer.getCurrentRank();

        if (game.getResult().equals(DRAW)) {
            updateRanksForDraw(higherRank, lowerRank);
        } else if (lowerRankedPlayerWon) {
            updateRanksForLowerRankedPlayerWinning(higherRank, lowerRank);
        } // else higher-ranked player won, so change nothing
    }

    private void updateRanksForLowerRankedPlayerWinning(int higherRank, int lowerRank) {
        // Demote higherRanked Player by promoting the player below him
        // (there is always a player below the higherRanked Player)
        playerRepository.promotePlayer(higherRank + 1, higherRank);

        // (I would have not allowed demoting the higer rank by two when
        // they are separated by 1, but the spec says *and*, so that is what I did..

        // PROMOTE the lower ranked player halfway to higher rank
        playerRepository.promotePlayer(lowerRank, lowerRank - calculateNumberOfPositionsToPromote(higherRank, lowerRank));
    }

    static int calculateNumberOfPositionsToPromote(int higherRank, int lowerRank) {
        // the spec was a little unclear wrt if we should round up or down.
        // I opted for rounding it down i.e. 2.5 -> 2
        // giving this effect:
        //    a b -> b a
        //    a b c -> b c a   - demoting higher rank twice :O
        //    a b c d -> b a d c
        //    a b c d e -> b a e c d
        // rounding up can cause weird jumps eg:
        //    a b c -> c b a    - both move 2
        //    a b c d -> b d a c  - both move 2
        return (lowerRank - higherRank) / 2;
//        return (int) Math.round((lowerRank - higherRank) / 2.0);
    }

    private void updateRanksForDraw(int higherRank, int lowerRank) {
        // move lover rank player up one if not adjacent (i.e. ranks differ by more than one)
        if (lowerRank - higherRank > 1) {
            playerRepository.promotePlayer(lowerRank, lowerRank - 1);
        } // else adjacent, so nothing to do
    }
}