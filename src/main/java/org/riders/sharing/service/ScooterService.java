package org.riders.sharing.service;

import org.riders.sharing.dto.PageRequestDto;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.dto.ScooterDto;
import org.riders.sharing.model.Scooter;

import java.util.UUID;

public interface ScooterService {
    PageResponseDto<ScooterDto> getAvailableScooters(PageRequestDto requestDto);
    Scooter getById(UUID id);
    Scooter rentScooter(Scooter scooter);
}
