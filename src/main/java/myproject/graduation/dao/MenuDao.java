package myproject.graduation.dao;

import lombok.AllArgsConstructor;
import myproject.graduation.dao.crud.CrudMenuRepository;
import myproject.graduation.model.Menu;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
@AllArgsConstructor
public class MenuDao {

    private final CrudMenuRepository crudMenuRepository;

    public Menu save(Menu menu) {
        return crudMenuRepository.save(menu);
    }

    public Menu getByDate(LocalDate date) {
        return crudMenuRepository.findByDate(date);
    }

    public void delete(int id) {
        crudMenuRepository.delete(id);
    }

    public List<Menu> getAllMenuById(Integer id) {
        return crudMenuRepository.getAllByRestaurantId(id);
    }

    public Menu getCurrentMenu(Integer id) {
        return crudMenuRepository.findByRestaurantIdAndDate(id, LocalDate.now());
    }
}
