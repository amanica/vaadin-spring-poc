package com.example.vaadinspringpoc.view.games;

import com.example.vaadinspringpoc.data.entity.Game;
import com.example.vaadinspringpoc.data.service.GameService;
import com.example.vaadinspringpoc.data.service.PlayerService;
import com.example.vaadinspringpoc.view.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;

import java.time.format.DateTimeFormatter;

//Only for testing:
@org.springframework.stereotype.Component
@Scope("prototype")

@Route(value = "games", layout= MainLayout.class)
@PageTitle("Games | Chess club")
public class GameListView extends VerticalLayout {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Grid<Game> grid = new Grid<>(Game.class);
    TextField filterText = new TextField();
    GameForm form;
    final GameService gameService;
    final PlayerService playerService;

    public GameListView(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);

        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new GameForm(playerService.findAllPlayersOrderByLastname(null));
        form.setWidth("25em");
        form.addSaveListener(this::saveGame);
//        form.addDeleteListener(this::deleteGame);
        form.addCloseListener(e -> closeEditor());
    }

    private void configureGrid() {
        grid.addClassNames("game-grid");
        grid.setSizeFull();
        grid.removeAllColumns();
        grid.addColumn(game -> game.getDateTime().format(DATE_TIME_FORMATTER))
                .setHeader("Date Time");
        grid.addColumns("whitePlayer.fullNameAndRank", "blackPlayer.fullNameAndRank", "result.caption");
        grid.getColumnByKey("whitePlayer.fullNameAndRank").setHeader("White");
        grid.getColumnByKey("blackPlayer.fullNameAndRank").setHeader("Black");
        grid.getColumnByKey("result.caption").setHeader("Result");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event ->
                editGame(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addGameButton = new Button("Add game");
        addGameButton.addClickShortcut(Key.KEY_A, KeyModifier.ALT);
        addGameButton.setTooltipText("Add a new game (Alt+a)");
        addGameButton.addClickListener(click -> addGame());

        var toolbar = new HorizontalLayout(filterText, addGameButton);

        toolbar.addClassName("toolbar");

        return toolbar;
    }

    public void editGame(Game game) {
        if (game == null) {
            closeEditor();
        } else {
            form.setGame(game);
            form.setVisible(true);
            form.setEnabled(false);
            addClassName("editing");
            form.whitePlayer.focus();
        }
    }

    private void closeEditor() {
        form.setGame(null);
        form.setVisible(false);
        removeClassName("editing");
        filterText.focus();
    }

    private void addGame() {
        grid.asSingleSelect().clear();
        editGame(new Game());
        form.setEnabled(true);
    }

    private void saveGame(GameForm.SaveEvent event) {
        gameService.saveGame(event.getGame());
        updateList();
        closeEditor();
    }

// Delete is not supported at the moment, maybe it can be supported in future
//    private void deleteGame(GameForm.DeleteEvent event) {
//        // TODO: ask for confirmation
//        gameService.deleteGame(event.getGame());
//        updateList();
//        closeEditor();
//    }

    private void updateList() {
        grid.setItems(gameService.findAllGamesWithFilter(filterText.getValue()));
    }
}