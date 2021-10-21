package myproject.graduation.dao.crud;

import myproject.graduation.entity.Voice;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional(readOnly = true)
public interface CrudVoiceRepository extends BaseRepository<Voice> {

//    Voice findByUserIdAndDateTime_Date(Integer userId, LocalDate date);
}
