package com.example.vaadinspringpoc.data.entity;

import com.example.vaadinspringpoc.data.converter.MonthDayIntegerAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Month;
import java.time.MonthDay;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, of = {})
@ToString(callSuper = true, of = {"firstName", "lastName"})
@Entity
public class Player extends AbstractEntity {

    @NotEmpty
    private String firstName = "";

    @NotEmpty
    private String lastName = "";

    @Email
    @NotEmpty
    private String email = "";

    @NotNull
    private Integer gamesPlayed = 0;

    @NotNull
    @Column(unique=true)
    private Integer currentRank = 0;

    @Column(columnDefinition = "mediumint")
    @Convert(converter = MonthDayIntegerAttributeConverter.class)
    private MonthDay birthday;

    public Month getBirthdayMonth() {
        if (birthday == null) {
            return null;
        } else {
            return birthday.getMonth();
        }
    }

    public void setBirthdayMonth(Month month) {
        if (month == null) {
            birthday = null;
        } else if (birthday == null) {
            birthday = MonthDay.of(month, 1);
        } else {
            birthday = MonthDay.of(month, birthday.getDayOfMonth());
        }
    }

    public Integer getBirthdayDay() {
        if (birthday == null) {
            return null;
        } else {
            return birthday.getDayOfMonth();
        }
    }

    public void setBirthdayDay(Integer day) {
        if (day == null) {
            birthday = null;
        } else if (birthday == null) {
                birthday = MonthDay.of(Month.JANUARY, day);
        } else {
            birthday = MonthDay.of(birthday.getMonth(), day);
        }
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
