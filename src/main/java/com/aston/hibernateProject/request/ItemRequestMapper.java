package com.aston.hibernateProject.request;

import com.aston.hibernateProject.request.dto.ItemRequestDto;
import com.aston.hibernateProject.request.model.ItemRequest;
import com.aston.hibernateProject.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

public class ItemRequestMapper {
    public static ItemRequestDto returnItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(Collections.emptyList())
                .build();
    }

    public static ItemRequest returnItemRequest(ItemRequestDto itemRequestDto, User user) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requester(user)
                .build();
    }
}