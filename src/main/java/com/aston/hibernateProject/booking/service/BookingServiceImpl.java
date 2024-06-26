package com.aston.hibernateProject.booking.service;


import com.aston.hibernateProject.item.ItemRepository;
import com.aston.hibernateProject.item.model.Item;
import com.aston.hibernateProject.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aston.hibernateProject.booking.BookingMapper;
import com.aston.hibernateProject.booking.BookingRepository;
import com.aston.hibernateProject.booking.BookingState;
import com.aston.hibernateProject.booking.Status;
import com.aston.hibernateProject.booking.dto.BookingDto;
import com.aston.hibernateProject.booking.dto.OutputBookingDto;
import com.aston.hibernateProject.booking.model.Booking;
import com.aston.hibernateProject.exception.NotFoundException;
import com.aston.hibernateProject.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public OutputBookingDto addBooking(BookingDto bookingDto, long userId) {

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Позиция с ID %d не найдена", bookingDto.getItemId())));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID %d не найден", userId)));
        Booking booking = BookingMapper.returnBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        if (item.getOwner().equals(user)) {
            throw new NotFoundException(String.format("Владелец с ID %d не может бронировать свой предмет", userId));
        }
        if (!item.getAvailable()) {
            throw new NotFoundException(String.format("Позиция с ID %d уже забронирована", item.getId()));
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new NotFoundException("Начало не может быть после конца");
        }
        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new NotFoundException("Начало не может совпадать с концом");
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public OutputBookingDto approveBooking(long userId, long bookingId, Boolean approved) {

        checkBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException(String.format("Только владелец с ID %d предмета может изменить статус брони", userId));
        }
        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new NotFoundException("Некорректный статус");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }


    @Override
    @Transactional(readOnly = true)
    public OutputBookingDto getBookingById(long userId, long bookingId) {

        checkBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();

        checkUser(userId);

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException(String.format("Получить информацию о брони c id = %d может получить только владелец и арендатор c id = %d.", bookingId, userId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutputBookingDto> getBookingsByBookerId(long userId, String state, Integer from, Integer size) {

        checkUser(userId);
        PageRequest pageRequest = checkPageSize(from, size);
        Page<Booking> bookings = null;
        BookingState bookingState = BookingState.getEnumValue(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageRequest);
                break;

        }
        return BookingMapper.returnBookingDtoList(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutputBookingDto> getBookingsForItemsByOwnerId(long userId, String state, Integer from, Integer size) {

        checkUser(userId);
        PageRequest pageRequest = checkPageSize(from, size);
        if (itemRepository.findByOwnerId(userId).isEmpty()) {
            throw new NotFoundException(String.format("У пользователя c id = %d нет позиций для брони", userId));
        }
        Page<Booking> bookings = null;
        BookingState bookingState = BookingState.getEnumValue(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageRequest);
                break;
        }
        return BookingMapper.returnBookingDtoList(bookings);
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
    }

    private void checkBooking(long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException(String.format("Бронь с ID %d не найдена", bookingId));
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
}