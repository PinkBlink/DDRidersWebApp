package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.PageRequestDto;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.dto.ScooterDto;
import org.riders.sharing.exception.BadRequestException;
import org.riders.sharing.exception.NoElementException;
import org.riders.sharing.exception.IllegalStatusException;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.utils.ValidationUtils;

import java.util.UUID;

import static java.lang.Math.min;
import static java.lang.Math.max;
import static org.riders.sharing.model.enums.ScooterStatus.AVAILABLE;
import static org.riders.sharing.model.enums.ScooterStatus.RENTED;


public class ScooterServiceImpl implements ScooterService {
    private static final Logger LOGGER = LogManager.getLogger(ScooterServiceImpl.class);
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

    @Override
    public Scooter getById(UUID id) {
        final var maybeScooter = scooterRepository.findById(id);

        return maybeScooter.orElseThrow(() -> {
            LOGGER.error("Couldn't find scooter with id {}", id);
            return new NoElementException("Couldn't find scooter with id %s".formatted(id));
        });
    }

    @Override
    public Scooter rentScooter(Scooter scooter) {
        ValidationUtils.checkThat(
            scooter.getStatus().equals(AVAILABLE),
            () -> new IllegalStatusException("Scooter is already rented")
        );

        final var updatedScooter = scooter.toBuilder()
            .status(RENTED)
            .build();

        return scooterRepository.update(updatedScooter);
    }

    @Override
    public Scooter releaseScooter(Scooter scooter) {
        ValidationUtils.checkThat(
            scooter.getStatus().equals(RENTED),
            () -> new IllegalStatusException("Scooter %s is already available".formatted(scooter.getId()))
        );

        final var updatedScooter = scooter.toBuilder()
            .status(AVAILABLE)
            .build();

        return scooterRepository.update(updatedScooter);

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
