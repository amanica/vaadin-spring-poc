package com.example.vaadinspringpoc.view;

import com.example.vaadinspringpoc.view.games.GameListView;
import com.example.vaadinspringpoc.view.leaderboard.LeaderboardView;
import com.example.vaadinspringpoc.view.players.PlayerListView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout { 

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Chess club");
        logo.addClassNames(
            LumoUtility.FontSize.LARGE, 
            LumoUtility.Margin.MEDIUM);

        var header = new HorizontalLayout(new DrawerToggle(), logo ); 

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER); 
        header.setWidthFull();
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header); 
    }

    private void createDrawer() {
        addToDrawer(new VerticalLayout( 
                new RouterLink("Leaderboard", LeaderboardView.class),
                new RouterLink("Games", GameListView.class),
                new RouterLink("Players", PlayerListView.class)
        ));
    }
}