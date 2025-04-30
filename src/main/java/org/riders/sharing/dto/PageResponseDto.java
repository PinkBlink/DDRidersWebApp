package org.riders.sharing.dto;

import java.util.List;

public record PageResponseDto<T extends EntityDto>(List<T> elements, int page, int pageSize,
                                                   int totalElements, int totalPages) {
}
