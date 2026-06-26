package kz.askar.shop.service;

import kz.askar.shop.dao.CharacteristicValueDao;
import kz.askar.shop.entity.CharacteristicValue;
import kz.askar.shop.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacteristicValueService {

    private static final Logger logger = LoggerFactory.getLogger(CharacteristicValueService.class);
    private final CharacteristicValueDao characteristicValueDao;

    public CharacteristicValueService(CharacteristicValueDao characteristicValueDao) {
        this.characteristicValueDao = characteristicValueDao;
    }

    public List<CharacteristicValue> findByProduct(Product product) {
        logger.debug("Finding characteristic values for product: {}", product.getId());
        return characteristicValueDao.findByProduct(product);
    }

    public void save(CharacteristicValue characteristicValue) {
        logger.info("Saving characteristic value");
        characteristicValueDao.save(characteristicValue);
    }
}
