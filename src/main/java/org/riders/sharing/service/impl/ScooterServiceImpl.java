package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.PageRequestDto;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.dto.ScooterDto;
import org.riders.sharing.exception.NotFoundException;
import org.riders.sharing.exception.IllegalStatusException;
import org.riders.sharing.model.Scooter;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.utils.ErrorMessages;
import org.riders.sharing.utils.PaginationUtils;
import org.riders.sharing.utils.ValidationUtils;

import java.util.UUID;

import static org.riders.sharing.model.enums.ScooterStatus.AVAILABLE;
import static org.riders.sharing.model.enums.ScooterStatus.RENTED;
import static org.riders.sharing.utils.ErrorMessages.SCOOTER_ALREADY_AVAILABLE;
import static org.riders.sharing.utils.ErrorMessages.SCOOTER_ALREADY_RENTED;
import static org.riders.sharing.utils.ErrorMessages.SCOOTER_NOT_FOUND;


public class ScooterServiceImpl implements ScooterService {
    private static final Logger LOGGER = LogManager.getLogger(ScooterServiceImpl.class);

    private final ScooterRepository scooterRepository;

    public ScooterServiceImpl(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    @Override
    public PageResponseDto<ScooterDto> getAvailableScooters(PageRequestDto requestDto) {
        final var page = PaginationUtils.definePage(requestDto.page());
        final var pageSize = PaginationUtils.definePageSize(requestDto.pageSize());
        final var offset = PaginationUtils.defineOffset(page, pageSize);
        final var totalElements = scooterRepository.getAvailableScootersAmount();
        final var totalPages = PaginationUtils.calculateTotalPages(totalElements, pageSize);

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
            return new NotFoundException(SCOOTER_NOT_FOUND.formatted(id));
        });
    }

    @Override
    public Scooter rentScooter(Scooter scooter) {
        ValidationUtils.checkThat(
            scooter.getStatus().equals(AVAILABLE),
            () -> new IllegalStatusException(SCOOTER_ALREADY_RENTED)
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
            () -> new IllegalStatusException(SCOOTER_ALREADY_AVAILABLE.formatted(scooter.getId()))
        );

        final var updatedScooter = scooter.toBuilder()
            .status(AVAILABLE)
            .build();

        return scooterRepository.update(updatedScooter);

    }
}
