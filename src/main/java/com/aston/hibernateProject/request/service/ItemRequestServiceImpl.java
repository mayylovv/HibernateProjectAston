package com.aston.hibernateProject.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aston.hibernateProject.exception.NotFoundException;
import com.aston.hibernateProject.item.ItemMapper;
import com.aston.hibernateProject.item.ItemRepository;
import com.aston.hibernateProject.item.model.Item;
import com.aston.hibernateProject.request.ItemRequestMapper;
import com.aston.hibernateProject.request.ItemRequestRepository;
import com.aston.hibernateProject.request.dto.ItemRequestDto;
import com.aston.hibernateProject.request.model.ItemRequest;
import com.aston.hibernateProject.user.UserRepository;
import com.aston.hibernateProject.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, long userId) {
        checkUser(userId);
        User user = userRepository.findById(userId).get();
        ItemRequest itemRequest = ItemRequestMapper.returnItemRequest(itemRequestDto, user);
        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.returnItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByUserId(long userId) {

        checkUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterIdOrderByCreatedAsc(userId);
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(addItemsToRequest(itemRequest));
        }
        return result;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {

        PageRequest pageRequest = checkPageSize(from, size);
        Page<ItemRequest> itemRequests = itemRequestRepository.findByIdIsNotOrderByCreatedAsc(userId, pageRequest);
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(addItemsToRequest(itemRequest));
        }
        return result;
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {

        checkUser(userId);
        checkRequest(requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).get();
        return addItemsToRequest(itemRequest);

    }

    @Override
    public ItemRequestDto addItemsToRequest(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestMapper.returnItemRequestDto(itemRequest);
        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
        itemRequestDto.setItems(ItemMapper.returnItemDtoList(items));
        return itemRequestDto;
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
    }

    private PageRequest checkPageSize(Integer from, Integer size) {
        if (from == 0 && size == 0) {
            throw new NotFoundException("size и from не должны быть равны нулю");
        }
        if (size <= 0) {
            throw new NotFoundException("size должен быть больше нуля");
        }
        if (from < 0) {
            throw new NotFoundException("from должно быть больше или равно нулю");
        }
        return PageRequest.of(from / size, size);
    }

    private void checkRequest(Long requestId) {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundException(String.format("Запрос с id = %d не найден", requestId));
        }
    }

}