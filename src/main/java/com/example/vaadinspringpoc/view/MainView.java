package com.example.vaadinspringpoc.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("") // map view to the root
@PageTitle("Main | Chess club")
class MainView extends VerticalLayout {
    MainView() {
        add(new H1("Chess club administration app"));
    }
}