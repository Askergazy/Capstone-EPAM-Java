package kz.askar.shop.service;

import kz.askar.shop.dao.UserDao;
import kz.askar.shop.entity.User;
import kz.askar.shop.security.UserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailService.class);
    private final UserDao userDao;

    public UserDetailService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", login);
        Optional<User> user = userDao.findByLogin(login);

        if (user.isEmpty()) {
            logger.warn("User not found: {}", login);
            throw new UsernameNotFoundException("User not found");
        }

        return new UserDetail(user.get());
    }
}
