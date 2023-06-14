package com.example.vaadinspringpoc.data.service;

import com.example.vaadinspringpoc.data.entity.Game;
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
            Integer whiteStartRank = game.getWhitePlayer().getCurrentRank();
            game.setWhiteStartRank(whiteStartRank);

            Integer blackStartRank = game.getBlackPlayer().getCurrentRank();
            game.setBlackStartRank(blackStartRank);

            // TODO: update ranks
            if (game.getResult().equals(DRAW)) {
                // move lover rank player up one if not adjacent
                if (whiteStartRank < blackStartRank - 1) {
                    playerRepository.promotePlayer(blackStartRank, blackStartRank - 1);
                } else if (blackStartRank < whiteStartRank - 1) {
                    playerRepository.promotePlayer(whiteStartRank, whiteStartRank - 1);
                } // else adjacent, so nothing to do
            }
        } else {
            throw new IllegalArgumentException("Updating games are not supported at the moment.");
        }
        gameRepository.save(game);
    }
}