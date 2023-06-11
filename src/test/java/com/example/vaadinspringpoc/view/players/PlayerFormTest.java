package com.example.vaadinspringpoc.view.players;

import com.example.vaadinspringpoc.data.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class PlayerFormTest {

    public static final String FIRST_NAME = "Hans";
    public static final String LAST_NAME = "Niemann";
    public static final String EMAIL = "hans.niemann@example.com";
    private Player player;
    
    @BeforeEach
    public void setupData() {
        player = new Player();
        player.setFirstName(FIRST_NAME);
        player.setLastName(LAST_NAME);
        player.setEmail(EMAIL);
    }

    @Test
    public void formFieldsPopulated() {
        //given
        PlayerForm form = new PlayerForm();

        //when
        form.setPlayer(player);

        //then
        assertThat(form.firstName.getValue(), equalTo(FIRST_NAME));
        assertThat(form.lastName.getValue(), equalTo(LAST_NAME));
        assertThat(form.email.getValue(), equalTo(EMAIL));
    }

    @Test
    public void saveEventHasCorrectValues() {
        //given
        PlayerForm form = new PlayerForm();
        Player player = new Player();
        form.setPlayer(player);

        form.firstName.setValue(FIRST_NAME);
        form.lastName.setValue(LAST_NAME);
        form.email.setValue(EMAIL);

        AtomicReference<Player> savedPlayerRef = new AtomicReference<>(null);
        form.addSaveListener(e -> {
            savedPlayerRef.set(e.getPlayer());
        });

        //when
        form.save.click();

        //then
        Player savedPlayer = savedPlayerRef.get();
        assertThat(savedPlayer.getFirstName(), equalTo(FIRST_NAME));
        assertThat(savedPlayer.getLastName(), equalTo(LAST_NAME));
        assertThat(savedPlayer.getEmail(), equalTo(EMAIL));
    }
}