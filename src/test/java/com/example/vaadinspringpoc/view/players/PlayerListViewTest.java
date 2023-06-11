package com.example.vaadinspringpoc.view.players;

import com.example.vaadinspringpoc.data.entity.Player;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssume.assumeThat;

@SpringBootTest
class PlayerListViewTest {

    @Autowired
    private PlayerListView playerListView;

    @Test
    public void formShownWhenPlayerSelected() {
        //given
        Grid<Player> grid = playerListView.grid;
        Player firstPlayer = getFirstItem(grid);

        PlayerForm form = playerListView.form;
        assumeThat(form.isVisible(), is(false));

        //when
        grid.asSingleSelect().setValue(firstPlayer);

        //then
        assumeThat(form.isVisible(), is(true));
        assumeThat(form.firstName.getValue(), equalTo(firstPlayer.getFirstName()));
    }

    private Player getFirstItem(Grid<Player> grid) {
        return( (ListDataProvider<Player>) grid.getDataProvider()).getItems().iterator().next();
    }
}