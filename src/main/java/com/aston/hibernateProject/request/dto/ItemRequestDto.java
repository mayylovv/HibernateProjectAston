package com.aston.hibernateProject.request.dto;

import com.aston.hibernateProject.item.dto.ItemDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {

    long id;
    @NotNull(message = "Description cannot be empty")
    @NotBlank(message = "Description cannot be blank")
    String description;
    LocalDateTime created;
    List<ItemDto> items;
}