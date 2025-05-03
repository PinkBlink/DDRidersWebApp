package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.PageRequestDto;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.dto.ScooterDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.service.ScooterService;

import static java.lang.Math.min;
import static java.lang.Math.max;


public class ScooterServiceImpl implements ScooterService {
    private static final int DEFAULT_PAGE = 1;
    private static final int MAX_PAGE_SIZE = 1000;

    private final ScooterRepository scooterRepository;

    public ScooterServiceImpl(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    @Override
    public PageResponseDto<ScooterDto> getAvailableScooters(PageRequestDto requestDto) {
        final var page = definePage(requestDto);
        final var pageSize = definePageSize(requestDto);
        final var offset = defineOffset(page, pageSize);
        final var totalElements = scooterRepository.getAvailableScootersAmount();
        final var totalPages = calculateTotalPages(totalElements, pageSize);

        final var scooterDtoList = scooterRepository
            .findAvailableScootersForResponse(pageSize, offset)
            .stream()
            .map(ScooterDto::fromScooter)
            .toList();

        return new PageResponseDto<>(
            scooterDtoList,
            page,
            pageSize,
            totalElements,
            totalPages
        );
    }

    private int calculateTotalPages(long totalElements, int pageSize) {
        if (pageSize <= 0) {
            throw new BadRequestException("Value pageSize must be > 0;");
        }
        return (int) ((totalElements + pageSize - 1) / pageSize);
    }

    private int definePage(PageRequestDto requestDto) {
        return max(requestDto.page(), DEFAULT_PAGE);
    }

    private int definePageSize(PageRequestDto requestDto) {
        return min(
            max(requestDto.size(), 1),
            MAX_PAGE_SIZE
        );
    }

    private int defineOffset(int page, int pageSize) {
        return (page - 1) * pageSize;
    }
}
