package org.riders.sharing.service;

import org.riders.sharing.dto.PageRequestDto;
import org.riders.sharing.dto.PageResponseDto;
import org.riders.sharing.dto.ScooterDto;

public interface ScooterService {
    PageResponseDto<ScooterDto> getAvailableScooters(PageRequestDto requestDto);
}
