package com.aston.hibernateProject.comment;

import com.aston.hibernateProject.comment.dto.CommentDto;
import com.aston.hibernateProject.comment.model.Comment;
import com.aston.hibernateProject.item.model.Item;
import com.aston.hibernateProject.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static List<CommentDto> returnCommentDtoList(Iterable<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(returnCommentDto(comment));
        }
        return result;
    }

    public static CommentDto returnCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public static Comment returnComment(CommentDto commentDto, Item item, User user, LocalDateTime dateTime) {
        return Comment.builder()
                .text(commentDto.getText())
                .created(dateTime)
                .item(item)
                .author(user)
                .build();
    }
}