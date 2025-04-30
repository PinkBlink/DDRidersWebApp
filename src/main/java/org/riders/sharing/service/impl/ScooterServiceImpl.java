package org.riders.sharing.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.riders.sharing.dto.PageRequestDto;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.dto.ScooterDto;
import org.riders.sharing.exception.NoElementException;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.service.ScooterService;

import java.util.ArrayList;

public class ScooterServiceImpl implements ScooterService {
    private static final Logger logger = LogManager.getLogger(ScooterServiceImpl.class);
    private final ScooterRepository scooterRepository;

    public ScooterServiceImpl(ScooterRepository scooterRepository) {
        this.scooterRepository = scooterRepository;
    }

    @Override
    public PageResponseDto<ScooterDto> getAvailableScooters(PageRequestDto requestDto) {
        final var scooterDtoList = new ArrayList<ScooterDto>();
        final var page = requestDto.page();
        final var pageSize = requestDto.size();
        final var offset = (page - 1) * pageSize;
        final var totalElements = scooterRepository.getAvailableScootersAmount();
        final var totalPages = (int) Math.ceil((double) totalElements / pageSize);
        final var availableScooterList = scooterRepository.findAvailableScootersForResponse(pageSize, offset);

        if (availableScooterList.isEmpty()) {
            logger.error("Couldn't find available scooters");
            throw new NoElementException("Couldn't find available scooters");
        }

        availableScooterList.forEach(scooter -> scooterDtoList.add(ScooterDto.fromScooter(scooter)));
        return new PageResponseDto<>(scooterDtoList, page, pageSize, totalElements, totalPages);
    }
}
