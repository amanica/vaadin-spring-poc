package com.example.vaadinspringpoc.data.service;

import com.example.vaadinspringpoc.data.entity.Player;
import com.example.vaadinspringpoc.data.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> findAllPlayers(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) { 
            return playerRepository.findAll();
        } else {
            return playerRepository.search(stringFilter);
        }
    }

    public long countPlayers() {
        return playerRepository.count();
    }

    public void deletePlayer(Player player) {
        playerRepository.delete(player);
    }

    public void savePlayer(Player player) {
        if (player == null) {
            log.error("Player is null.");
            return;
        }
        playerRepository.save(player);
    }
}