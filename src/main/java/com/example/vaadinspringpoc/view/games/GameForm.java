package com.example.vaadinspringpoc.view.games;

import com.example.vaadinspringpoc.data.entity.Game;
import com.example.vaadinspringpoc.data.entity.GameResult;
import com.example.vaadinspringpoc.data.entity.Player;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.util.List;

public class GameForm extends FormLayout {

    ComboBox<Player> whitePlayer;
    ComboBox<Player> blackPlayer;
    RadioButtonGroup<GameResult> result;

    Button save = new Button("Save");
//    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Game> binder = new BeanValidationBinder<>(Game.class);

    public GameForm(List<Player> players) {
        addClassName("game-form");
        createFields(players);
        binder.forField(whitePlayer)
                .withValidator(
                        player -> !player.equals(blackPlayer.getValue()),
                        "Game needs two (distinct) players.")
                .bind(Game::getWhitePlayer, Game::setWhitePlayer);
        binder.forField(blackPlayer)
                .withValidator(
                        player -> !player.equals(whitePlayer.getValue()),
                        "Game needs two (distinct) players.")
                .bind(Game::getBlackPlayer, Game::setBlackPlayer);
        binder.bindInstanceFields(this);

        add(whitePlayer,
            blackPlayer,
            result,
            createButtonsLayout());
    }

    private void createFields(List<Player> players) {
        whitePlayer = new ComboBox<>("White", players);
        blackPlayer = new ComboBox<>("Black", players);
        result = new RadioButtonGroup<>("Result", GameResult.values());

        whitePlayer.setItemLabelGenerator(Player::getFullName);
        blackPlayer.setItemLabelGenerator(Player::getFullName);
        result.setItemLabelGenerator(GameResult::getCaption);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        save.setTooltipText("Save game (ENTER)");
        close.addClickShortcut(Key.ESCAPE);
        close.setTooltipText("Cancel (ESC)");

        save.addClickListener(event -> validateAndSave());
//        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save //, delete
                , close);
    }

    private void validateAndSave() {
        if (binder.isValid() && binder.validate().isOk()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    public void setGame(Game game) {
        binder.setBean(game);
    }

    // Events
    @Getter
    public static abstract class GameFormEvent extends ComponentEvent<GameForm> {
        private final Game game;

        protected GameFormEvent(GameForm source, Game game) {
            super(source, false);
            this.game = game;
        }
    }

    public static class SaveEvent extends GameFormEvent {
        SaveEvent(GameForm source, Game game) {
            super(source, game);
        }
    }

//    public static class DeleteEvent extends GameFormEvent {
//        DeleteEvent(GameForm source, Game game) {
//            super(source, game);
//        }
//    }

    public static class CloseEvent extends GameFormEvent {
        CloseEvent(GameForm source) {
            super(source, null);
        }
    }

//    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
//        return addListener(DeleteEvent.class, listener);
//    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
