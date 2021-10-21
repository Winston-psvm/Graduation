package myproject.graduation.dao;

import lombok.AllArgsConstructor;
import myproject.graduation.dao.crud.CrudMealRepository;
import myproject.graduation.entity.Meal;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class MealDAO {
    private final CrudMealRepository crudMealRepository;

    @Transactional
    public Meal save(Meal meal) {
        return crudMealRepository.save(meal);
    }

    public boolean delete(int id) {
        return crudMealRepository.delete(id) != 0;
    }


}
