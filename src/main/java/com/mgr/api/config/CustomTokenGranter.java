package com.mgr.api.config;

import com.mgr.api.service.impl.UserServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

//Bạn dùng CustomTokenGranter để mở rộng khả năng của OAuth2.
// Thay vì chỉ đăng nhập bằng User/Pass, bạn ép người dùng phải
// khai báo thêm họ thuộc về tổ chức nào (tenant). Nếu không có class này,
// Spring sẽ vứt bỏ trường tenant của bạn vì nó không hiểu trường đó là gì.
public class CustomTokenGranter extends AbstractTokenGranter {
    private UserServiceImpl userService;
    private AuthenticationManager authenticationManager;

    protected CustomTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
    }

    public CustomTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType, UserServiceImpl userService) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        return super.getOAuth2Authentication(client, tokenRequest);
    }

    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        String username = tokenRequest.getRequestParameters().get("username");
        String password = tokenRequest.getRequestParameters().get("password");
        String tenant = tokenRequest.getRequestParameters().get("tenant");
        try {
            if (SecurityConstant.GRANT_TYPE_CUSTOM.equalsIgnoreCase(tokenRequest.getGrantType())) {
                return userService.getAccessTokenForCustom(client, tokenRequest, username, password, tenant, tokenRequest.getGrantType(), this.getTokenServices());
            } else if (!Objects.equals(tokenRequest.getGrantType(), SecurityConstant.GRANT_TYPE_PASSWORD)) {
                throw new InvalidTokenException("Invalid grant type: " + tokenRequest.getGrantType());
            }
            return null;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new InvalidTokenException("account or tenant invalid");
        }
    }
}
