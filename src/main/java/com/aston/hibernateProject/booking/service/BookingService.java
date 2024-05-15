package com.aston.hibernateProject.booking.service;

import com.aston.hibernateProject.booking.dto.BookingDto;
import com.aston.hibernateProject.booking.dto.OutputBookingDto;

import java.util.List;


public interface BookingService {

    OutputBookingDto addBooking(BookingDto bookingDto, long userId);

    OutputBookingDto approveBooking(long userId, long bookingId, Boolean approved);

    OutputBookingDto getBookingById(long userId, long bookingId);

    List<OutputBookingDto> getBookingsByBookerId(long userId, String state, Integer from, Integer size);

    List<OutputBookingDto> getBookingsForItemsByOwnerId(long userId, String state, Integer from, Integer size);
}