package net.ammensa.repository;

import net.ammensa.entity.Menu;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.springframework.stereotype.Repository;

import java.util.logging.Logger;

@Repository
public class MenuRepository {

    private static final Logger LOGGER = Logger.getLogger(MenuRepository.class.getName());

    private ObjectRepository<Menu> nitriteRepository;

    {
        Nitrite db = Nitrite.builder().openOrCreate();
        this.nitriteRepository = db.getRepository(Menu.class);
        this.nitriteRepository.register(changeInfo -> LOGGER.info("menu change: " + changeInfo.getChangeType()));
    }

    public void save(Menu menu) {
        nitriteRepository.insert(menu);
    }

    public Menu retrieve() {

        Cursor<Menu> cursor = nitriteRepository.find();

        if (cursor.size() == 0) {
            return new Menu();
        }

        return cursor.iterator().next();
    }

    public void delete() {
        nitriteRepository.remove((ObjectFilter) null);
    }
}
