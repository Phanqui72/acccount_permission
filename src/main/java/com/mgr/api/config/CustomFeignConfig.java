package com.mgr.api.config;

import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

@Configuration
@ConditionalOnClass({Feign.class})
//if we don't config using this anotation, spring boot won't detect @FeignClient and don't know
// autowired (null)
@EnableFeignClients(basePackages = "com.mgr.api.external") //enable feign client
@Slf4j
public class CustomFeignConfig {
    @Bean
    public Contract feignContract() {
        return new SpringMvcContract();
    }

    // Thêm Bean này để thấy Log gọi API bên ngoài trong Console
    //API bên ngoài thường rất khó kiểm soát. Khi bạn bật Log mức FULL,
    // bạn sẽ thấy trong Console toàn bộ
    // URL, Header và Body mà Feign gửi đi/nhận về. Rất hữu ích khi demo bị lỗi.
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * TÙY CHỌN: Nếu bạn dùng Feign để gọi các Service khác nội bộ
     * mà cần truyền cái Token hiện tại đi cùng, hãy dùng Interceptor này.
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
                OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
                requestTemplate.header("Authorization", "Bearer " + details.getTokenValue());
            }
        };
    }
}
