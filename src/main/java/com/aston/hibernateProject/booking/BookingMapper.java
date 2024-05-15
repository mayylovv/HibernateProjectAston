package com.aston.hibernateProject.booking;

import com.aston.hibernateProject.booking.dto.ShortItemBookingDto;
import com.aston.hibernateProject.booking.model.Booking;
import com.aston.hibernateProject.item.ItemMapper;
import com.aston.hibernateProject.user.UserMapper;
import com.aston.hibernateProject.booking.dto.BookingDto;
import com.aston.hibernateProject.booking.dto.OutputBookingDto;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static Booking returnBooking(BookingDto bookingDto) {
        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
        if (bookingDto.getStatus() == null) {
            booking.setStatus(Status.WAITING);
        } else {
            booking.setStatus(bookingDto.getStatus());
        }
        return booking;
    }

    public static List<OutputBookingDto> returnBookingDtoList(Iterable<Booking> bookings) {
        List<OutputBookingDto> result = new ArrayList<>();
        for (Booking booking : bookings) {
            result.add(toBookingDto(booking));
        }
        return result;
    }

    public static OutputBookingDto toBookingDto(Booking booking) {
        return OutputBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemMapper.returnItemDto(booking.getItem()))
                .booker(UserMapper.returnUserDto(booking.getBooker()))
                .build();
    }

    public static ShortItemBookingDto toBookingShortDto(Booking booking) {
        return ShortItemBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}