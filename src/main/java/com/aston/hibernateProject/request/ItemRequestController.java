package com.aston.hibernateProject.request;

import com.aston.hibernateProject.request.dto.ItemRequestDto;
import com.aston.hibernateProject.request.service.ItemRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;



@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@RequestHeader(HEADER_USER_ID) Long userId,
                                                        @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return ResponseEntity.ok(itemRequestService.createRequest(itemRequestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getRequests(@RequestHeader(HEADER_USER_ID) Long userId) {
        return ResponseEntity.ok(itemRequestService.getAllRequestsByUserId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(@RequestHeader(HEADER_USER_ID) Long userId,
                                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                                               @RequestParam(defaultValue = "10", required = false) Integer size) {
        return ResponseEntity.ok(itemRequestService.getAllRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader(HEADER_USER_ID) Long userId,
                                                         @PathVariable("requestId") Long requestId) {
        return ResponseEntity.ok(itemRequestService.getRequestById(userId, requestId));
    }
}