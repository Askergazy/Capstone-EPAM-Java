package kz.askar.shop.service;


import kz.askar.shop.dao.UserDao;
import kz.askar.shop.entity.Role;
import kz.askar.shop.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) {
        logger.info("Registering new user: {}", user.getLogin());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        userDao.save(user);
    }

}