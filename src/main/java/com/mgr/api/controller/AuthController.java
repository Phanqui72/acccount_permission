package com.mgr.api.controller;

import com.mgr.api.dto.ApiMessageDto;
import com.mgr.api.external.InternalClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends ABasicController {

    @Autowired
    private InternalClient internalClient;

    @PostMapping("/login-internal")
    public ApiMessageDto<Map<String, Object>> loginInternal(@RequestBody Map<String, String> body) {
        // Tạo mã Basic Auth từ Client ID và Secret (thay bằng thông tin thật của bạn)
        String clientId = "abc_client";
        String clientSecret = "abc123";
        String authHeader = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        // Gọi Feign Client
        // Lưu ý: params trong body cần có: username, password, grant_type
        Map<String, Object> tokenResponse = internalClient.getAccessTokenInternal(authHeader, body);

        return makeSuccessResponse(tokenResponse, "Login success");
    }
}