package com.example.vaadinspringpoc.data.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(of = {"firstName", "lastName"})
@Entity
public class Player extends AbstractEntity {

    @NotEmpty
    private String firstName = "";

    @NotEmpty
    private String lastName = "";

    @Email
    @NotEmpty
    private String email = "";

    @NotEmpty
    private Integer gamesPlayed = 0;

    @NotEmpty
    private Integer currentRank = 0;

//TODO: Birthday
}