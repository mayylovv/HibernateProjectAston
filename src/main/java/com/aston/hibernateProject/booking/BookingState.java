package com.aston.hibernateProject.booking;


import com.aston.hibernateProject.exception.NotFoundException;
import lombok.Getter;


@Getter
public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState getEnumValue(String state) {

        try {
            return BookingState.valueOf(state);
        } catch (Exception e) {
            throw new NotFoundException("Unknown state: " + state);
        }

    }
}