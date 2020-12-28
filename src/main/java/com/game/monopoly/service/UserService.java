package com.game.monopoly.service;

import com.game.monopoly.domain.Role;
import com.game.monopoly.domain.User;
import com.game.monopoly.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String domain = "localhost:8080";

    public void save(User user){
        userRepo.saveAndFlush(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null){
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public boolean isUsernameUnique(String username){
        return userRepo.findByUsername(username) == null;
    }

    public boolean isEmailUnique(String email){
        return userRepo.findByEmail(email) == null;
    }

    public boolean addUser(User user){

        if (!isUsernameUnique(user.getUsername()) || !isEmailUnique(user.getEmail())){
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        LocalDate todayLocalDate = LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(todayLocalDate);
        user.setRegisterDate(sqlDate);

        user.setWins(0);
        user.setDefeats(0);

        userRepo.save(user);

        return true;
    }

    public boolean sendActivationMessage(User user) {
        if (!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Monopoly Online. Please, visit next link: http://%s/activate/%s",
                    user.getUsername(),
                    domain,
                    user.getActivationCode()
            );

            try {
                mailSender.send(user.getEmail(), "Activation code", message);
                return true;
            }
            catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if (user == null){
            return false;
        }

        user.setActivationCode(null);

        userRepo.save(user);

        return true;
    }

    public void winCount(String winnerName){
        User user = userRepo.findByUsername(winnerName);
        user.winCount();
        userRepo.save(user);
    }

    public void defeatCount(User user){
        user.defeatCount();
        userRepo.save(user);
    }

    public void defineDefeats(List<String> names){
        for (String name : names){
            User user = userRepo.findByUsername(name);
            user.defeatCount();
            userRepo.save(user);
        }
    }

    public List<User> getRating(){
        return userRepo.findAll(new Sort(Sort.Direction.DESC, "wins"));
    }

}
