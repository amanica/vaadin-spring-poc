package com.example.vaadinspringpoc.data.converter;

import com.example.vaadinspringpoc.data.entity.GameResult;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GameResultConverter implements AttributeConverter<GameResult, String> {

        @Override
        public String convertToDatabaseColumn(GameResult vehicle) {
            return vehicle.getCode();
        }

        @Override
        public GameResult convertToEntityAttribute(String dbData) {
            return GameResult.fromCode(dbData);
        }

}
