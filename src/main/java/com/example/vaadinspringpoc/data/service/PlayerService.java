package com.example.vaadinspringpoc.data.service;

import com.example.vaadinspringpoc.data.entity.Player;
import com.example.vaadinspringpoc.data.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> findAllPlayersOrderByLastname(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) { 
            return playerRepository.findAll(Sort.by(Sort.Direction.ASC, "lastName"));
        } else {
            return playerRepository.search(stringFilter);
        }
    }

    public List<Player> findAllPlayersOrderByRank() {
        return playerRepository.findAll(Sort.by(Sort.Direction.ASC, "currentRank"));
    }

    public Player getById(Long id) {
        return playerRepository.findById(id).orElseThrow();
    }

    public long countPlayers() {
        return playerRepository.count();
    }

    @Transactional
    public void deletePlayer(Player player) {
        playerRepository.delete(player);
        playerRepository.decrementRanks(player.getCurrentRank());
    }

    @Transactional
    public void savePlayer(Player player) {
        if (player == null) {
            log.error("Player is null.");
            return;
        }
        if (player.getCurrentRank() == 0) {
            // Business rule:
            // New players will, by default, be ranked last.
            player.setCurrentRank((int) countPlayers() + 1);
        }
        playerRepository.save(player);
    }
}