package myproject.graduation.dao.crud;

import myproject.graduation.entity.User;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CrudUserRepository extends BaseRepository<User> {

    User getByEmail(String email);
}
