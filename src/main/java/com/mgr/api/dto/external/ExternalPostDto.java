package com.mgr.api.dto.external;

import lombok.Data;

@Data
public class ExternalPostDto {
    private Long id;
    private Long userId;
    private String title;
    private String body;
}