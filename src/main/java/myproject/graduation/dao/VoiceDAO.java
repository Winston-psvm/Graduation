package myproject.graduation.dao;

import lombok.AllArgsConstructor;
import myproject.graduation.dao.crud.CrudVoiceRepository;
import myproject.graduation.model.Voice;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Repository
@AllArgsConstructor
public class VoiceDAO  {

    private final CrudVoiceRepository crudVoiceRepository;


    public List<Voice> getAllUserVotes(Integer userId) {
        return crudVoiceRepository.findAllByUserIdOrderByDateTimeDesc(userId);
    }

    public Voice getCurrentUserVoice(Integer userId, LocalDate date) {
        return crudVoiceRepository.findByUserIdAndDateTimeGreaterThanEqualAndDateTimeLessThanEqual(userId, date.atStartOfDay(), date.plus(1, ChronoUnit.DAYS).atStartOfDay());
    }

    public Voice save(Voice voice) {
        return crudVoiceRepository.save(voice);
    }

    public void delete(Integer id) {
        crudVoiceRepository.delete(id);
    }

    public List<Voice> getCurrentVoices( LocalDate date) {
        return crudVoiceRepository.findAllByDateTimeGreaterThanEqualAndDateTimeLessThanEqualOrderByDateTimeDesc(date.atStartOfDay(), date.plus(1, ChronoUnit.DAYS).atStartOfDay());
    }


}
