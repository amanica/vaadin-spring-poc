package com.example.vaadinspringpoc.data.converter;

import jakarta.persistence.AttributeConverter;

import java.time.MonthDay;

/**
 * Source: https://stackoverflow.com/a/60699637/381083
 */
public class MonthDayIntegerAttributeConverter implements AttributeConverter<MonthDay, Integer> {

    @Override
    public Integer convertToDatabaseColumn(MonthDay attribute) {
        return (attribute.getMonthValue() * 100) + attribute.getDayOfMonth();
    }

    @Override
    public MonthDay convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        int month = dbData / 100;
        int day = dbData % 100;
        return MonthDay.of(month, day);
    }
}