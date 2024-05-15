package com.aston.hibernateProject.item.service;

import com.aston.hibernateProject.booking.Status;
import com.aston.hibernateProject.booking.model.Booking;
import com.aston.hibernateProject.comment.CommentMapper;
import com.aston.hibernateProject.comment.CommentRepository;
import com.aston.hibernateProject.comment.dto.CommentDto;
import com.aston.hibernateProject.comment.model.Comment;
import com.aston.hibernateProject.exception.NotFoundException;
import com.aston.hibernateProject.item.ItemMapper;
import com.aston.hibernateProject.item.ItemRepository;
import com.aston.hibernateProject.item.dto.ItemDto;
import com.aston.hibernateProject.item.model.Item;
import com.aston.hibernateProject.request.ItemRequestRepository;
import com.aston.hibernateProject.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aston.hibernateProject.booking.BookingMapper;
import com.aston.hibernateProject.booking.BookingRepository;

import com.aston.hibernateProject.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ItemDto addItem(long userId, ItemDto itemDto) {
        checkUser(userId);
        User user = userRepository.findById(userId).get();
        Item item = ItemMapper.returnItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            checkRequest(itemDto.getRequestId());
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).get());
        }
        itemRepository.save(item);
        return ItemMapper.returnItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(long itemId, long userId, ItemDto itemDto) {
        checkUser(userId);
        User user = userRepository.findById(userId).get();
        checkItem(itemId);
        Item item = ItemMapper.returnItem(itemDto, user);
        item.setId(itemId);
        if (!itemRepository.findByOwnerId(userId).contains(item)) {
            throw new NotFoundException("Объект не был найден у пользователя с id =  " + userId);
        }
        Item newItem = itemRepository.findById(item.getId()).get();
        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }
        itemRepository.save(newItem);
        return ItemMapper.returnItemDto(newItem);
    }

    @Override
    @Transactional
    public ItemDto getItemById(long itemId, long userId) {
        checkItem(itemId);
        Item item = itemRepository.findById(itemId).get();
        ItemDto itemDto = ItemMapper.returnItemDto(item);
        checkUser(userId);
        if (item.getOwner().getId() == userId) {
            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, LocalDateTime.now());
            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }
            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }
        }
        List<Comment> commentList = commentRepository.findByItemId(itemId);
        if (!commentList.isEmpty()) {
            itemDto.setComments(CommentMapper.returnCommentDtoList(commentList));
        } else {
            itemDto.setComments(Collections.emptyList());
        }
        return itemDto;
    }

    @Override
    @Transactional
    public List<ItemDto> getItemsByUserId(long userId, Integer from, Integer size) {
        checkUser(userId);
        PageRequest pageRequest = checkPageSize(from, size);
        List<ItemDto> resultList = new ArrayList<>();
        for (ItemDto itemDto : ItemMapper.returnItemDtoList(itemRepository.findByOwnerId(userId, pageRequest))) {
            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }
            resultList.add(itemDto);
        }
        for (ItemDto itemDto : resultList) {
            List<Comment> commentList = commentRepository.findByItemId(itemDto.getId());
            if (!commentList.isEmpty()) {
                itemDto.setComments(CommentMapper.returnCommentDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }
        return resultList;
    }

    @Override
    @Transactional
    public List<ItemDto> getItemsByKeyword(String text, Integer from, Integer size) {
        PageRequest pageRequest = checkPageSize(from, size);
        if (text.equals("")) {
            return Collections.emptyList();
        } else {
            return ItemMapper.returnItemDtoList(itemRepository.search(text, pageRequest));
        }
    }

    @Override
    @Transactional
    public CommentDto postComment(long userId, long itemId, CommentDto commentDto) {
        checkUser(userId);
        User user = userRepository.findById(userId).get();

        checkItem(itemId);
        Item item = itemRepository.findById(itemId).get();

        LocalDateTime dateTime = LocalDateTime.now();

        Optional<Booking> booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, Status.APPROVED, dateTime);

        if (commentDto.getText().isEmpty()) {
            throw new NotFoundException("Невозможно опубликовать пустой комментарий");
        }
        if (booking.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не бронировал позицию с id = %d.", userId, itemId));
        }

        Comment comment = CommentMapper.returnComment(commentDto, item, user, dateTime);
        commentRepository.save(comment);

        return CommentMapper.returnCommentDto(comment);
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    private void checkItem(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Позиция с id = " + itemId + " не найдена");
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
            throw new NotFoundException("Запрос с id = " + requestId + " не найден.");
        }
    }
}