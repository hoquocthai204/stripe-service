package com.example.stripe.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.stripe.service.dto.request.OrderRequestDto;
import com.example.stripe.service.dto.response.OrderResponseDto;
import com.example.stripe.service.dto.response.OrderStatusResponseDto;

import lombok.NonNull;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto dto, @NonNull HttpServletRequest httpServletRequest);

    OrderStatusResponseDto checkOrderStatus(Long orderId, HttpServletRequest httpServletRequest);

    OrderResponseDto getOrder(Long orderId);

    List<OrderResponseDto> getAllOrder();

}
