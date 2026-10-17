package com.k955.kdex.security.oauth2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OAuth2UserInfo {
    private String email;
    private String name;
    private String picture;
    private String providerId;
}
