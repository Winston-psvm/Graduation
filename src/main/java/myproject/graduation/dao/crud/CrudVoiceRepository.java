package myproject.graduation.dao.crud;

import myproject.graduation.model.Voice;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CrudVoiceRepository extends BaseRepository<Voice> {

//    Voice findByUserIdAndDateTime_Date(Integer userId, LocalDate date);
}
