package com.example.vaadinspringpoc.view.players;

import com.example.vaadinspringpoc.data.entity.Player;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlayerForm extends FormLayout {

    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    EmailField email = new EmailField("Email");
    ComboBox<Month> birthdayMonth;
    ComboBox<Integer> birthdayDay;

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Player> binder = new BeanValidationBinder<>(Player.class);

    public PlayerForm() {
        addClassName("player-form");
        Component birthDayPickers = createBirthDayPickers();

        binder.forField(birthdayMonth)
                .bind(Player::getBirthdayMonth,
                        Player::setBirthdayMonth);
        binder.forField(birthdayDay)
                .bind(Player::getBirthdayDay,
                        Player::setBirthdayDay);
        binder.bindInstanceFields(this);
        add(firstName,
                lastName,
                email,
                birthDayPickers,
                createButtonsLayout());
    }
    
    private Component createBirthDayPickers() {
        birthdayMonth = new ComboBox<>("Birthday Month", Month.values());
        birthdayMonth.setRequired(false);
        birthdayMonth.setItemLabelGenerator(
                m -> m.getDisplayName(TextStyle.FULL, Locale.getDefault()));
        birthdayMonth.setWidth(9, Unit.EM);
        birthdayMonth.addValueChangeListener(e -> {
            updateBirthdayDay();
        });

        birthdayDay = new ComboBox<>("Day");
        birthdayDay.setRequired(false);
        birthdayDay.setWidth(5, Unit.EM);
        birthdayDay.setEnabled(false);

        HorizontalLayout horizontalLayout = new HorizontalLayout(birthdayMonth, birthdayDay);
        return horizontalLayout;
    }

    private void updateBirthdayDay() {
        birthdayDay.setEnabled(true);
        if (birthdayMonth.getValue() != null) {
            int lengthOfMonth = birthdayMonth.getValue().maxLength();
            birthdayDay.setItems(IntStream.range(1, lengthOfMonth + 1).boxed()
                    .collect(Collectors.toList()));

            if (binder.getBean() != null) {
                birthdayDay.setValue(binder.getBean().getBirthdayDay());
            }
        }
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    public void setPlayer(Player player) {
        binder.setBean(player);
    }

    // Events
    @Getter
    public static abstract class PlayerFormEvent extends ComponentEvent<PlayerForm> {
        private final Player player;

        protected PlayerFormEvent(PlayerForm source, Player player) {
            super(source, false);
            this.player = player;
        }
    }

    public static class SaveEvent extends PlayerFormEvent {
        SaveEvent(PlayerForm source, Player player) {
            super(source, player);
        }
    }

    public static class DeleteEvent extends PlayerFormEvent {
        DeleteEvent(PlayerForm source, Player player) {
            super(source, player);
        }

    }

    public static class CloseEvent extends PlayerFormEvent {
        CloseEvent(PlayerForm source) {
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
