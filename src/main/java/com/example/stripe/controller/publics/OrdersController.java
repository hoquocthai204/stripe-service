package com.example.stripe.controller.publics;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stripe.service.OrderService;
import com.example.stripe.service.dto.request.OrderRequestDto;
import com.example.stripe.service.dto.response.OrderResponseDto;
import com.example.stripe.service.dto.response.OrderStatusResponseDto;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequestMapping("/orders")
@RestController
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @ApiOperation(value = "API to create order")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successully"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @PostMapping(produces = "application/vn.sparkminds.api-v1+json")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto dto,
            @NonNull HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(orderService.createOrder(dto, httpServletRequest));
    }

    @ApiOperation(value = "API to check order status")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successully"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @GetMapping(value = "/check-status/{orderId}", produces = "application/vn.sparkminds.api-v1+json")
    public ResponseEntity<OrderStatusResponseDto> checkOrderStatus(@PathVariable Long orderId,
            @NonNull HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok().body(orderService.checkOrderStatus(orderId, httpServletRequest));
    }

    @ApiOperation(value = "API to get order")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successully"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @GetMapping(value = "/{orderId}", produces = "application/vn.sparkminds.api-v1+json")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long orderId,
            @NonNull HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @ApiOperation(value = "API to get all order")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successully"),
            @ApiResponse(code = 500, message = "Internal server error") })
    @GetMapping(produces = "application/vn.sparkminds.api-v1+json")
    public ResponseEntity<List<OrderResponseDto>> getAllOrder() {
        return ResponseEntity.ok(orderService.getAllOrder());
    }
}
