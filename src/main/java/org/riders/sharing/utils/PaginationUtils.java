package org.riders.sharing.utils;

import org.riders.sharing.exception.BadRequestException;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.riders.sharing.utils.ErrorMessages.PAGE_SIZE_IS_ZERO;

public class PaginationUtils {
    private static final int DEFAULT_PAGE = 1;
    private static final int MAX_PAGE_SIZE = 1000;

    public static int calculateTotalPages(long totalElements, int pageSize) {
        if (pageSize <= 0) {
            throw new BadRequestException(PAGE_SIZE_IS_ZERO);
        }
        return (int) ((totalElements + pageSize - 1) / pageSize);
    }

    public static int definePage(int page) {
        return max(page, DEFAULT_PAGE);
    }

    public static int definePageSize(int pageSize) {
        return min(
            max(pageSize, 1),
            MAX_PAGE_SIZE
        );
    }

    public static int defineOffset(int page, int pageSize) {
        return (page - 1) * pageSize;
    }
}
