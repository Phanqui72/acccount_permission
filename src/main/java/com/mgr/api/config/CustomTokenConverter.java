package com.mgr.api.config;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.Map;

//Nó là bộ chuyển đổi mặc định giúp chuyển đổi qua lại giữa một đối tượng Java (OAuth2AccessToken)
// và một cấu trúc dữ liệu kiểu bản đồ (Map<String, ?>) để có thể biến thành JSON hoặc chuỗi JWT.
public class CustomTokenConverter extends DefaultAccessTokenConverter {
    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
        // 1. Gọi hàm mặc định để lấy lại User và Quyền hạn (Authorities)
        OAuth2Authentication authentication
                = super.extractAuthentication(claims);
        //Lấy tất cả những gì có trong thẻ nhét hết vào Details
        authentication.setDetails(claims);
        return authentication;
    }
}

//2. Hai nhiệm vụ chính (Dựa trên Method Detail)
//Tài liệu tập trung vào hai quá trình ngược nhau:
//A. Quá trình "Đóng gói" (convertAccessToken)
//Đầu vào: Đối tượng OAuth2AccessToken (chứa ngày hết hạn, mã token...).
//Đầu ra: Một Map (dạng key-value).
//Mục đích: Để chuẩn bị dữ liệu trước khi biến nó thành chuỗi JWT gửi cho Frontend. Nó sẽ nhét các trường chuẩn như exp (hết hạn), user_name, authorities (quyền hạn) vào Map này.
//B. Quá trình "Mở gói" (extractAuthentication) — QUAN TRỌNG NHẤT
//Đầu vào: Một Map chứa thông tin đã được giải mã từ chuỗi JWT.
//Đầu ra: Đối tượng OAuth2Authentication.
//Mục đích: Khi người dùng cầm Token quay lại gọi API, hệ thống dùng hàm này để đọc dữ liệu từ Token và dựng lại "hồ sơ" người dùng để biết họ là ai.