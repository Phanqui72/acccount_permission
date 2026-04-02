package com.mgr.api.external;

import com.mgr.api.dto.ApiMessageDto;
import com.mgr.api.dto.ResponseListDto;
import com.mgr.api.dto.category.CategoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@FeignClient(name = "internal-client", url = "http://localhost:8787") // Tự gọi chính mình
public interface InternalClient {

    // Gọi API token mặc định của OAuth2
    @PostMapping(value = "/api/token", consumes = "application/json")
    Map<String, Object> getAccessTokenInternal(
            @RequestHeader("Authorization") String basicAuth,
            @RequestBody Map<String, ?> params
    );

    // Gọi API list category của chính mình
    @GetMapping("/v1/category/list")
    ApiMessageDto<ResponseListDto<List<CategoryDto>>> getCategories(@RequestHeader("Authorization") String bearerToken);
}