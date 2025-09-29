package hello.pet.authservice.application.service;

import hello.pet.authservice.adapter.out.dto.LoginValidationResponse;
import hello.pet.authservice.adapter.out.security.JwtTokenProvider;
import hello.pet.authservice.application.exception.LoginCredentialException;
import hello.pet.authservice.application.port.in.LoginUseCase;
import hello.pet.authservice.application.port.in.command.LoginCommand;
import hello.pet.authservice.application.port.out.AuthPort;
import hello.pet.authservice.application.port.out.query.LoginQuery;
import hello.pet.authservice.application.port.out.result.LoginResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase {

    private final AuthPort authPort;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;

    @Override
    public LoginResult login(LoginCommand cmd) {
        LoginValidationResponse res = authPort.validationRequest(new LoginQuery(cmd.email(), cmd.password()));
        if (!res.valid()) {
            throw new LoginCredentialException("이메일 혹은 패스워드가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(res.email(), res.id(), res.role());
        String refreshToken = jwtTokenProvider.generateRefreshToken(res.email(), res.id(), res.role());

        return LoginResult.success(accessToken, refreshToken, tokenPrefix, res.id(), res.nickname(), res.role(), res.profileUrl());
    }
}
