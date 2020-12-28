package com.game.monopoly.repos;

import com.game.monopoly.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo  extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    User findByActivationCode(String code);

}
