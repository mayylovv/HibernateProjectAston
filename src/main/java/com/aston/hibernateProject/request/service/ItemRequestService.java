package com.aston.hibernateProject.request.service;

import com.aston.hibernateProject.request.dto.ItemRequestDto;
import com.aston.hibernateProject.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getAllRequestsByUserId(long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getRequestById(long userId, long requestId);

    ItemRequestDto addItemsToRequest(ItemRequest itemRequest);
}