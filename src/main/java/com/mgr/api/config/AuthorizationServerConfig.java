package com.mgr.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgr.api.exception.BadRequestException;
import com.mgr.api.exception.NotFoundException;
import com.mgr.api.exception.oauth.CustomOauthException;
import com.mgr.api.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceImpl userService;

    @Value("${signing.key}")
    private String signingKey;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public TokenStore tokenStore() {
        JdbcTokenStore j = new JdbcTokenStore(Objects.requireNonNull(jdbcTemplate.getDataSource()));
        j.setAuthenticationKeyGenerator(new CustomAuthenticationKeyGenerator());
        return j;
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(new CustomTokenConverter());
        converter.setSigningKey(signingKey);
        return converter;
    }

    //Định nghĩa Client (Ứng dụng) nào được phép kết nối vào hệ thống.
    // lấy username là abc_123, nó xem client_id có tồn tại không
    @Override
    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
        configurer.jdbc(jdbcTemplate.getDataSource());
    }


    //Cấu hình các tính năng kỹ thuật của việc tạo Token (Token Store, tùy biến Token, loại đăng nhập).
    //Đây là nơi bạn thiết lập "máy in thẻ" hoạt động như thế nào. Nếu bạn muốn dùng luồng password,
    // bạn bắt buộc phải gắn AuthenticationManager vào đây.

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(new CustomTokenEnhancer(jdbcTemplate, objectMapper), accessTokenConverter()));
        endpoints
                //Đây là API dùng cho luồng Authorization Code (thường thấy khi bạn bấm "Đăng nhập bằng Google").
                // Nó dùng để hiển thị trang "Bạn có cho phép ứng dụng X truy cập dữ liệu của mình không?".
                .pathMapping("/oauth/authorize", "/api/authorize")
                //API Đăng nhập.
                .pathMapping("/oauth/token", "/api/token") //Đổi tên đường dẫn từ /oauth/token thành /api/token cho ngắn gọn.
                .authenticationManager(authenticationManager)
                .tokenEnhancer(tokenEnhancerChain)//Gắn thêm "gia vị" (thông tin người dùng) vào thẻ.
                .tokenGranter(tokenGranter(endpoints))
                .accessTokenConverter(accessTokenConverter())
                .tokenStore(tokenStore()) //họn nơi cất giữ thẻ (Database).
                .reuseRefreshTokens(false)
                .userDetailsService(userDetailsService)
                //Dịch các lỗi OAuth2 thô kệch thành các thông báo lịch sự mà bạn đã định nghĩa
                .exceptionTranslator((exception) -> {
                    if (exception instanceof NotFoundException) {
                        return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(new CustomOauthException(exception.getMessage(), ((NotFoundException) exception).getCode()));
                    }
                    if (exception instanceof BadRequestException) {
                        return ResponseEntity
                                .badRequest()
                                .body(new CustomOauthException(exception.getMessage(), ((BadRequestException) exception).getCode()));
                    } else if (exception instanceof OAuth2Exception) {
                        OAuth2Exception oAuth2Exception = (OAuth2Exception) exception;
                        return ResponseEntity
                                .status(oAuth2Exception.getHttpErrorCode())
                                .body(new CustomOauthException(oAuth2Exception.getMessage()));
                    } else {
                        throw exception;
                    }
                });
    }

    private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> granters = new ArrayList<TokenGranter>(Arrays.asList(endpoints.getTokenGranter()));
        granters.add(new CustomTokenGranter(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory(), SecurityConstant.GRANT_TYPE_CUSTOM, userService));
        return new CompositeTokenGranter(granters);
    }

    //Thiết lập bảo mật cho chính cái đường dẫn cấp thẻ (mặc định là /oauth/token).
    // nó kiểm tra quyền nào được xem token
    //allowFormAuthenticationForClients(): Cho phép gửi ID/Secret của ứng dụng qua form.
    //checkTokenAccess("permitAll()"): Cho phép mọi người có thể gọi vào để kiểm tra tính hợp lệ của Token.
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.allowFormAuthenticationForClients();
        oauthServer.tokenKeyAccess("hasAuthority('ROLE_TRUSTED_CLIENT')").checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
        oauthServer.checkTokenAccess("permitAll()");
    }
}