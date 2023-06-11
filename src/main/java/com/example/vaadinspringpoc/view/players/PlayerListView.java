package com.example.vaadinspringpoc.view.players;

import com.example.vaadinspringpoc.data.entity.Player;
import com.example.vaadinspringpoc.data.service.PlayerService;
import com.example.vaadinspringpoc.view.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "players", layout= MainLayout.class)
@PageTitle("Players | Chess club")
public class PlayerListView extends VerticalLayout {

    Grid<Player> grid = new Grid<>(Player.class);
    TextField filterText = new TextField();
    PlayerForm form;
    PlayerService playerService;

    public PlayerListView(PlayerService playerService) {
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
        form = new PlayerForm();
        form.setWidth("25em");
    }

    private void configureGrid() {
        grid.addClassNames("player-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email", "gamesPlayed", "currentRank");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event ->
                editPlayer(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addPlayerButton = new Button("Add player");
        addPlayerButton.addClickListener(click -> addPlayer());

        var toolbar = new HorizontalLayout(filterText, addPlayerButton);

        toolbar.addClassName("toolbar");

        return toolbar;
    }

    public void editPlayer(Player player) {
        if (player == null) {
            closeEditor();
        } else {
            form.setPlayer(player);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setPlayer(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addPlayer() {
        grid.asSingleSelect().clear();
        editPlayer(new Player());
    }

    private void updateList() {
        grid.setItems(playerService.findAllPlayersOrderByLastname(filterText.getValue()));
    }
}