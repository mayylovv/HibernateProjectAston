package com.aston.hibernateProject.item.service;

import com.aston.hibernateProject.item.dto.ItemDto;
import com.aston.hibernateProject.comment.dto.CommentDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto update(long itemId, long userId, ItemDto itemDto);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItemsByUserId(long userId, Integer from, Integer size);

    List<ItemDto> getItemsByKeyword(String text, Integer from, Integer size);

    CommentDto postComment(long userId, long itemId, CommentDto commentDto);

}