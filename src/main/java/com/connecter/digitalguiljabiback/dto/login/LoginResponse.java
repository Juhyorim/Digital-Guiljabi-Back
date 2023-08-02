package com.connecter.digitalguiljabiback.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
        private String token;
        private Long userPk;

}
