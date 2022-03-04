package com.challenge.taxi.pooling;

import com.challenge.taxi.pooling.model.Taxi;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class TestUtilities {
    protected static String mapObjectToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(object);
    }

    protected static <T> T mapFronJson(String json, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    protected static String generateTaxiListJsonWithNElements(int listSize) throws JsonProcessingException {
        final int MIN_TAXI_SEATS = 4;
        final int MAX_TAXI_SEATS = 6;
        List<Taxi> taxiList = new ArrayList<>();

        for(int i = 0; i < listSize; i++) {
            taxiList.add(new Taxi(i, getRandomNumberBetweenRange(MIN_TAXI_SEATS, MAX_TAXI_SEATS)));
        }

        return mapObjectToJson(taxiList);
    }

    protected static int getRandomNumberBetweenRange(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }
}
