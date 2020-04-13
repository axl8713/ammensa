package net.ammensa.repository;

import net.ammensa.entity.Menu;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class MenuRepository {

    private static final Logger LOGGER = Logger.getLogger(MenuRepository.class.getName());

    private final ObjectRepository<Menu> nitriteRepository;

    {
        Nitrite db = Nitrite.builder().openOrCreate();
        this.nitriteRepository = db.getRepository(Menu.class);
        this.nitriteRepository.register(changeInfo -> LOGGER.fine("menu change: " + changeInfo.getChangeType()));
    }

    public void save(Menu menu) {
        nitriteRepository.insert(menu);
    }

    public Optional<Menu> retrieve() {

        LOGGER.fine("repo retrieve");

        Cursor<Menu> cursor = nitriteRepository.find();

        if (cursor.size() == 0) {
            return Optional.empty();
        }

        return Optional.of(cursor.iterator().next());
    }

    public void delete() {
        nitriteRepository.remove((ObjectFilter) null);
    }
}