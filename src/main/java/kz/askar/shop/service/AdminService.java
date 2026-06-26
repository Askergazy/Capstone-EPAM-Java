package kz.askar.shop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @PreAuthorize("hasRole(ROLE.ADMIN)")
    public void doAdminStuff() {
        logger.info("Admin action executed");
    }

}
