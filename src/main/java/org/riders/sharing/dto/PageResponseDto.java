package org.riders.sharing.dto;

import java.util.List;

public record PageResponseDto<T extends Pageable>(
    List<T> elements,
    int page,
    int pageSize,
    int totalElements,
    int totalPages
) {
}
