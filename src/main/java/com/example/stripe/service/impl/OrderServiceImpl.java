package com.example.stripe.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stripe.entity.Orders;
import com.example.stripe.entity.UserSession;
import com.example.stripe.entity.Wallet;
import com.example.stripe.exception.OrderException;
import com.example.stripe.exception.UserException;
import com.example.stripe.exception.WalletException;
import com.example.stripe.repository.OrderRepository;
import com.example.stripe.repository.UserSessionRepository;
import com.example.stripe.repository.WalletRepository;
import com.example.stripe.service.OrderService;
import com.example.stripe.service.dto.request.OrderRequestDto;
import com.example.stripe.service.dto.response.OrderResponseDto;
import com.example.stripe.service.dto.response.OrderStatusResponseDto;
import com.example.stripe.service.mapper.OrderMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    @Value("${application.gateway.url}")
    private String gatewayUrl;

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserSessionRepository sessionRepository;
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto, HttpServletRequest httpServletRequest) {
        String authorizationToken = httpServletRequest.getHeader("Authorization").substring(7);
        UserSession userSession = sessionRepository.findByToken(authorizationToken)
                .orElseThrow(() -> new UserException("User session is not found"));
        Optional<Wallet> wallet = walletRepository.findByUserId(userSession.getUser().getId());

        if (wallet.isEmpty())
            throw new WalletException("Wallet is not found");

        Orders orders = orderRepository.save(orderMapper.toEntity(dto));
        Orders order2 = orderRepository.findById(orders.getId())
                .orElseThrow(() -> new OrderException("Create order fail"));
        order2.setQrContent(orders.getId().toString());
        return orderMapper.toDtoNoProduct(order2, gatewayUrl + "qr-code/" + order2.getId().toString());
    }

    @Override
    public OrderStatusResponseDto checkOrderStatus(Long orderId, HttpServletRequest httpServletRequest) {

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order is not found"));

        return OrderStatusResponseDto.builder()
                .status(order.getStatus())
                .build();
    }

    @Override
    public OrderResponseDto getOrder(Long orderId) {
        Orders orders = orderRepository.findById(orderId).orElseThrow(() -> new OrderException("Order is not found"));
        return orderMapper.toDto(orders, gatewayUrl + "qr-code/" + orders.getId().toString());
    }

    @Override
    public List<OrderResponseDto> getAllOrder() {
        return orderRepository.findAll().stream()
                .map((e) -> orderMapper.toDto(e, gatewayUrl + "qr-code/" + e.getId().toString()))
                .collect(Collectors.toList());
    }
}
