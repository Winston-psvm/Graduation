package myproject.graduation.dao;

import lombok.AllArgsConstructor;
import myproject.graduation.dao.crud.CrudVoiceRepository;
import myproject.graduation.model.Voice;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
@AllArgsConstructor
public class VoiceDAO  {

    private final CrudVoiceRepository crudVoiceRepository;


    public List<Voice> getAllUserVotes(Integer userId) {
        return crudVoiceRepository.findAllByUserIdOrderByDateTimeDesc(userId);
    }

    public List<Voice> getAllRestVotes(Integer restId) {
        return crudVoiceRepository.findAllByRestaurantIdOrderByDateTimeDesc(restId);
    }

    public Voice getCurrentUserVoice(Integer userId, LocalDate date) {
        LocalDateTime dateTime = LocalDateTime.from(date);
        return crudVoiceRepository.getByUserIdAndDateTime(userId, dateTime);
    }

    public Voice save(Voice voice) {
        return crudVoiceRepository.save(voice);
    }

    public void delete(Integer id) {
        crudVoiceRepository.delete(id);
    }

    public List<Voice> getCurrentVoices( LocalDate date) {
        LocalDateTime dateTime = LocalDateTime.from(date);
        return crudVoiceRepository.findAllByDateTime(dateTime);
    }


}
