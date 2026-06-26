package kz.askar.shop.service;


import kz.askar.shop.dao.CharacteristicDao;
import kz.askar.shop.entity.Category;
import kz.askar.shop.entity.Characteristic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacteristicService {

    private static final Logger logger = LoggerFactory.getLogger(CharacteristicService.class);
    private final CharacteristicDao characteristicDao;

    public CharacteristicService(CharacteristicDao characteristicDao) {
        this.characteristicDao = characteristicDao;
    }

    public List<Characteristic> findCharacteristicsByCategory(Category category) {
        logger.debug("Finding characteristics by category: {}", category.getName());
        return characteristicDao.findCharacteristicByCategory(category);
    }

}
