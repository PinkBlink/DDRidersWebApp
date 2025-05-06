package org.riders.sharing.dto;

public record CustomerOrdersRequestDto(
    String customerId,
    PageRequestDto pageRequestDto
) {
}
