package com.project.SuperC.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String email;
    private List<PriceTrackingRequestDto> requests;
}