package com.aston.hibernateProject.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import com.aston.hibernateProject.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Page<ItemRequest> findByIdIsNotOrderByCreatedAsc(long userId, PageRequest pageRequest);

    List<ItemRequest> findByRequesterIdOrderByCreatedAsc(long requesterId);
}