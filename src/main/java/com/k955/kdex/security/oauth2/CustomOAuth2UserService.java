package com.k955.kdex.security.oauth2;

import com.k955.kdex.entity.Transaction;
import com.k955.kdex.entity.User;
import com.k955.kdex.entity.Wallet;
import com.k955.kdex.enums.Transaction.Type;
import com.k955.kdex.enums.User.Provider;
import com.k955.kdex.enums.User.Role;
import com.k955.kdex.enums.User.Status;
import com.k955.kdex.repository.TransactionRepository;
import com.k955.kdex.repository.UserRepository;
import com.k955.kdex.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements org.springframework.security.oauth2.client.userinfo.OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        OAuth2UserInfo userInfo = extractUserInfo(userRequest, oAuth2User);
        String email = userInfo.getEmail();
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("Email not found in OAuth2 user info");
        }

        Optional<User> existingUserOpt = userRepository.findByEmail(email);
        User user;
        if (existingUserOpt.isPresent()) {
            User existing = existingUserOpt.get();
            if (existing.getProvider() == Provider.LOCAL) {
                throw new OAuth2AuthenticationException("This email is already registered with password login. Please sign in with email/password.");
            }
            user = existing;
        } else {
            user = createUserWithWalletAndBonus(userInfo);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                oAuth2User.getAttributes(),
                "email");
    }

    private OAuth2UserInfo extractUserInfo(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if ("google".equalsIgnoreCase(registrationId)) {
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String picture = (String) attributes.get("picture");
            String sub = (String) attributes.get("sub");
            return OAuth2UserInfo.builder().email(email).name(name).picture(picture).providerId(sub).build();
        }
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String sub = (String) attributes.getOrDefault("sub", UUID.randomUUID().toString()).toString();
        return OAuth2UserInfo.builder().email(email).name(name).picture((String) attributes.get("picture")).providerId(sub).build();
    }

    private User createUserWithWalletAndBonus(OAuth2UserInfo userInfo) {
        User user = User.builder()
                .fullName(userInfo.getName())
                .email(userInfo.getEmail())
                .password(null)
                .role(Role.USER)
                .status(Status.ACTIVE)
                .provider(Provider.GOOGLE)
                .providerId(userInfo.getProviderId())
                .build();
        User savedUser = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .balance(BigDecimal.valueOf(1500.0000))
                .build();
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .user(savedUser)
                .type(Type.SIGNUP_BONUS)
                .amount(BigDecimal.valueOf(1500.0000))
                .balanceAfter(BigDecimal.valueOf(1500.0000))
                .build();
        transactionRepository.save(transaction);
        return savedUser;
    }
}
