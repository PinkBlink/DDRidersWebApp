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
    private static final Logger logger = LogManager.getLogger(ScooterServiceImpl.class);
    private final ScooterRepository scooterRepository;

    public ScooterServiceImpl(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    @Override
    public PageResponseDto<ScooterDto> getAvailableScooters(PageRequestDto requestDto) {

        final var page = max(requestDto.page(), DEFAULT_PAGE);
        int pageSize = min(
            max(requestDto.size(), 1),
            MAX_PAGE_SIZE
        );
        final var offset = (page - 1) * pageSize;
        final var totalElements = scooterRepository.getAvailableScootersAmount();
        final var totalPages = calculateTotalPages(totalElements, pageSize);
        final var scooterDtoList = scooterRepository
            .findAvailableScootersForResponse(pageSize, offset)
            .stream()
            .map(ScooterDto::fromScooter)
            .toList();
        logger.info("Find {} available scooters", scooterDtoList.size());
        return new PageResponseDto<>(
            scooterDtoList,
            page,
            pageSize,
            totalElements,
            totalPages
        );
    }

    private int calculateTotalPages(long totalElements, int pageSize) {
        if (pageSize <= 0) throw new BadRequestException("Value pageSize must be > 0;");
        return (int) ((totalElements + pageSize - 1) / pageSize);
    }
}
