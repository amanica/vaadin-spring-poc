package com.example.vaadinspringpoc.data.service;

import com.example.vaadinspringpoc.data.entity.Game;
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
        // higher rank has lower number!
        if (whitePlayer.getCurrentRank() < blackPlayer.getCurrentRank()) {
            higherRankedPlayer = whitePlayer;
            lowerRankedPlayer = blackPlayer;
        } else {
            higherRankedPlayer = blackPlayer;
            lowerRankedPlayer = whitePlayer;
        }

        // TODO: update ranks
        if (game.getResult().equals(DRAW)) {
            updateRanksForDraw(higherRankedPlayer.getCurrentRank(),
                    lowerRankedPlayer.getCurrentRank());
        }
    }

    private void updateRanksForDraw(Integer higherRank, Integer lowerRank) {
        // move lover rank player up one if not adjacent (i.e. ranks differ by more than one)
        if (lowerRank - higherRank > 1) {
            playerRepository.promotePlayer(lowerRank, lowerRank - 1);
        } // else adjacent, so nothing to do
    }
}