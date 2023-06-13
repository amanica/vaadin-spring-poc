package com.example.vaadinspringpoc.data.service;

import com.example.vaadinspringpoc.data.entity.Game;
import com.example.vaadinspringpoc.data.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
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
        if (game.getId() == null) {
            // new game
            game.setDateTime(LocalDateTime.now());
            game.setWhiteStartRank(game.getWhitePlayer().getCurrentRank());
            game.setBlackStartRank(game.getBlackPlayer().getCurrentRank());
        } else {
            throw new IllegalArgumentException("Updating games are not supported at the moment.");
        }
        // TODO: update ranks
        gameRepository.save(game);
    }
}