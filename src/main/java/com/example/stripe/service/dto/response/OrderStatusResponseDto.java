package com.example.stripe.service.dto.response;

import com.example.stripe.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusResponseDto {
  private OrderStatus status;
}
