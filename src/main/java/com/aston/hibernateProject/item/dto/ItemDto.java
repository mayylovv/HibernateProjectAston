package com.aston.hibernateProject.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import com.aston.hibernateProject.booking.dto.ShortItemBookingDto;
import com.aston.hibernateProject.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    Long id;
    @NotNull
    @NotBlank
    String name;
    @NotNull
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    ShortItemBookingDto lastBooking;
    ShortItemBookingDto nextBooking;
    List<CommentDto> comments;
    @Positive(message = "must be positive")
    Long requestId;
}