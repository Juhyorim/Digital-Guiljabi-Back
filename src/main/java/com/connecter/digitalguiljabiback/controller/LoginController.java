package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.OauthType;
import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.login.*;
import com.connecter.digitalguiljabiback.exception.UsernameDuplicatedException;
import com.connecter.digitalguiljabiback.security.oauth.kakao.KakaoClient;
import com.connecter.digitalguiljabiback.security.oauth.naver.NaverClient;
import com.connecter.digitalguiljabiback.service.JwtService;
import com.connecter.digitalguiljabiback.service.LoginService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "로그인", description = "로그인 관련 API입니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class LoginController {

  private final KakaoClient kakaoClient;
  private final NaverClient naverClient;
  private final LoginService loginService;
  private final JwtService jwtService;


   //카카오 로그인 페이지 url을 반환
  @GetMapping("/login/kakao")
  public ResponseEntity<Map> getKakaoLoginUrl() {
    Map<String, String> map = new HashMap<>();
    map.put("loginUrl", kakaoClient.getAuthUrl());
    return ResponseEntity.status(HttpStatus.CREATED).body(map);
  }

  /**
   * 카카오 로그인 콜백을 처리하고 인증에 성공한 경우 로그인 또는 회원 가입
   * @param authorizationCode 인가 코드
   * @return AuthResponseDTO 로그인 또는 회원 가입 결과를 담은 응답 DTO
   */
  @Hidden
  @GetMapping("/login/callback")
  public ResponseEntity<LoginResponse> processKakaoLoginCallback(@RequestParam("code") String authorizationCode) {
    AuthRequest params = new AuthRequest(authorizationCode);
    KakaoUserResponse response = kakaoClient.handleCallback(params);

    log.info("사용자 UID: {}", response.getUid());

    LoginResponse loginResponseDTO = loginService.loginOrCreate(response.toUserRequest(), OauthType.KAKAO);

    return ResponseEntity.ok(loginResponseDTO);
  }

  //네이버 로그인 페이지 url을 반환
  @GetMapping("/login/naver")
  public ResponseEntity<Map> getNaverLoginUrl() {
    Map<String, String> map = new HashMap<>();
    map.put("loginUrl", naverClient.getAuthUrl());
    return ResponseEntity.status(HttpStatus.CREATED).body(map);
  }

  /**
   * 네이버 로그인 콜백을 처리하고 인증에 성공한 경우 로그인 또는 회원 가입
   * @param authorizationCode 인가 코드
   * @param state 사이트 간 요청 위조 공격을 방지하기 위해 애플리케이션에서 생성한 상태 토큰값으로 URL 인코딩을 적용한 값을 사용
   * @return AuthResponseDTO 로그인 또는 회원 가입 결과를 담은 응답 DTO
   */
  @Hidden
  @GetMapping("/login/callback/naver")
  public ResponseEntity<LoginResponse> processNaverLoginCallback(
    @RequestParam("code") String authorizationCode,
    @RequestParam("state") String state
  ) {
    AuthRequest params = new AuthRequest(authorizationCode, state);
    NaverUserResponse response = naverClient.handleCallback(params);

    log.info("사용자 UID: {}", response.getId());

    LoginResponse loginResponseDTO = loginService.loginOrCreate(response.toUserRequest(), OauthType.NAVER);

    return ResponseEntity.ok(loginResponseDTO);
  }

  @PostMapping("/login/signup")
  public ResponseEntity tempSignUp(@RequestBody UserRequest userRequest) throws UsernameDuplicatedException {
    loginService.tempSignUp(userRequest);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> tempLogin(@RequestBody UserRequest userRequest) {
    LoginResponse loginResponse = loginService.tempLogin(userRequest);

    return ResponseEntity.ok(loginResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<Map> logout(@AuthenticationPrincipal Users user, @RequestHeader(value="Authorization") String authHeader) {
    OauthType type = user.getOauthType();
    Map<String, String> map = new HashMap<>();


    if (type == OauthType.KAKAO) {
        map.put("logoutUrl", kakaoClient.getLogoutUrl());
    }

    String token = authHeader.substring(7);
    
    //jwt토큰만료처리 -> 블랙리스트
    jwtService.addBlackList(token);

    return ResponseEntity.ok(map);
  }
}
