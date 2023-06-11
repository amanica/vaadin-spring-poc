package com.example.vaadinspringpoc.view.leaderboard;

import com.example.vaadinspringpoc.data.entity.Player;
import com.example.vaadinspringpoc.data.service.PlayerService;
import com.example.vaadinspringpoc.view.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value = "leaderboard", layout = MainLayout.class) 
@PageTitle("Leaderboard | Chess club")
public class LeaderboardView extends VerticalLayout {
    private final PlayerService playerService;
    Grid<Player> grid = new Grid<>(Player.class);

    public LeaderboardView(PlayerService playerService) {
        this.playerService = playerService;
        addClassName("leaderboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();
        configureGrid();
        add(getPlayerStats(), grid);
        updateList();
    }

    private Component getPlayerStats() {
        Span stats = new Span(playerService.countPlayers() + " players");
        stats.addClassNames(
            LumoUtility.FontSize.XLARGE,
            LumoUtility.Margin.Top.MEDIUM);
        return stats;
    }

    private void configureGrid() {
        grid.addClassNames("player-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "gamesPlayed", "currentRank");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(playerService.findAllPlayersOrderByRank());
    }
}