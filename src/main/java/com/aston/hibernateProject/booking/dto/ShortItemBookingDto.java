package com.aston.hibernateProject.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShortItemBookingDto {

    Long id;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
}