package com.example.vaadinspringpoc.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, of = {"whitePlayer", "blackPlayer", "result"})
@Entity
public class Game extends AbstractEntity {

    @NotNull
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "white_player_id")
    @NotNull
    private Player whitePlayer;

    @ManyToOne
    @JoinColumn(name = "black_player_id")
    @NotNull
    private Player blackPlayer;

    @NotNull(message="Every game needs a result")
    private GameResult result;

    @NotNull
    private Integer whiteStartRank = 0;

    @NotNull
    private Integer blackStartRank = 0;

    @AssertTrue(message = "Game needs two (distinct) players.")
    public boolean isGameWithDifferentPlayers() {
        return whitePlayer != null && blackPlayer != null && !whitePlayer.equals(blackPlayer);
    }
}
