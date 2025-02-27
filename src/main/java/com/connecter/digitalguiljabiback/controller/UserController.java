package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.domain.Users;
import com.connecter.digitalguiljabiback.dto.user.ImgUrlRequest;
import com.connecter.digitalguiljabiback.dto.user.InfoRequest;
import com.connecter.digitalguiljabiback.dto.user.NicknameRequest;
import com.connecter.digitalguiljabiback.dto.user.response.AllUserResponse;
import com.connecter.digitalguiljabiback.dto.user.response.UsersInfoResponse;
import com.connecter.digitalguiljabiback.exception.UsernameDuplicatedException;
import com.connecter.digitalguiljabiback.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@Tag(name = "UserController", description = "User관련한 컨트롤러 입니다")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class UserController {
  private final UserService userService;

  @GetMapping("/users/info/my")
  public ResponseEntity<UsersInfoResponse> getUserInfo(@AuthenticationPrincipal Users user) {
    UsersInfoResponse userInfo = userService.getUserInfo(user);

    return ResponseEntity.ok(userInfo);
  }

  //닉네임 처음 등록
  @PostMapping("/users/info/nickname")
  public ResponseEntity changeUserNickname(
    @AuthenticationPrincipal Users user,
    @RequestBody @Valid NicknameRequest request
  ) throws NoSuchElementException, UsernameDuplicatedException {
    userService.changeUserNickname(user, request);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/users/nickname/{nickname}/exists")
  public ResponseEntity hasNickname(@PathVariable String nickname) {
    Map<String, Boolean> map = new HashMap<>();
    map.put("hasNickname", userService.hasNickname(nickname));

    return ResponseEntity.ok(map);
  }

  @PostMapping("/users/info/profile")
  public ResponseEntity changeProfileImg(
    @AuthenticationPrincipal Users user,
    @RequestBody @Valid ImgUrlRequest request
  ) throws NoSuchElementException {
    userService.changeProfileImg(user, request);

    return ResponseEntity.ok().build();
  }

  @PatchMapping("/users/info")
  public ResponseEntity changeInfo(@AuthenticationPrincipal Users user, @RequestBody InfoRequest reqeust) throws NoSuchElementException {
    userService.changeInfo(user, reqeust);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/users")
  public ResponseEntity deleteUser(@AuthenticationPrincipal Users user) throws NoSuchElementException {
    userService.delete(user);

    return ResponseEntity.ok().build();
  }

  //adminㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
  @GetMapping("/admin/users/all")
  public ResponseEntity<AllUserResponse> getAllUser(
    @RequestParam(required = false, defaultValue = "10") int pageSize,
    @RequestParam(required = false, defaultValue = "1") int page
  ) {
    AllUserResponse all = userService.getAll(pageSize, page);

    return ResponseEntity.ok(all);
  }





}
