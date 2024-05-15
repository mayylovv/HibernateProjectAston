package com.aston.hibernateProject.booking.dto;

import com.aston.hibernateProject.booking.Status;
import com.aston.hibernateProject.item.dto.ItemDto;
import com.aston.hibernateProject.user.dto.UserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutputBookingDto {

    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Status status;
    UserDto booker;
    ItemDto item;
}