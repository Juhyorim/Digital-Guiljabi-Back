package com.connecter.digitalguiljabiback.repository;

import com.connecter.digitalguiljabiback.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUid(String uid);

    Optional<Users> findByNickname(String nickname);
}
