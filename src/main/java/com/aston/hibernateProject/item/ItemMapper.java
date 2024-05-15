package com.aston.hibernateProject.item;

import com.aston.hibernateProject.item.dto.ItemDto;
import com.aston.hibernateProject.item.model.Item;
import com.aston.hibernateProject.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static List<ItemDto> returnItemDtoList(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(returnItemDto(item));
        }
        return result;
    }

    public static ItemDto returnItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;

    }

    public static Item returnItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
    }
}