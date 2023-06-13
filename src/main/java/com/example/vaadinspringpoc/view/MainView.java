package com.example.vaadinspringpoc.view;

import com.example.vaadinspringpoc.view.leaderboard.LeaderboardView;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Main | Chess club")
class MainView extends VerticalLayout implements BeforeEnterObserver {
    MainView() {
        // should never be seen as the root page redirects to leaderboard
        add(new H1("Chess club administration app"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        beforeEnterEvent.rerouteTo(LeaderboardView.class);
    }
}