package uz.ikhtidev.bukharatelefonbot.service;

import org.springframework.stereotype.Service;
import uz.ikhtidev.bukharatelefonbot.domain.Phone;
import uz.ikhtidev.bukharatelefonbot.repository.PhoneRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class PhoneService {

    private final PhoneRepository phoneRepository;

    public PhoneService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    public Phone savePhone(Phone phone) {
        return phoneRepository.save(phone);
    }

    public void deletePhone(Phone phone) {
        phoneRepository.deleteById(phone.getId());
    }

    public List<Phone> getInactivePhonesByOwnerId(Long userId) {
        return phoneRepository.findByOwnerIdAndIsActive(userId, false);
    }

    public Phone getPhoneById(Long phoneId) {
        return phoneRepository.findById(phoneId).orElse(null);
    }

    public List<Phone> getPhonesForCurrentMonth() {
        int currentMonthValue = LocalDateTime.now().getMonthValue();
        // Oyning birinchi kuni
        LocalDateTime startDateTime = LocalDateTime.of(LocalDateTime.now().getYear(), currentMonthValue, 1, 0, 0);
        // Oyning oxirgi kuni
        LocalDateTime endDateTime = startDateTime.withDayOfMonth(startDateTime.getMonth().length(startDateTime.toLocalDate().isLeapYear()));

        return phoneRepository.findByPostedAtBetween(startDateTime,endDateTime);
    }
}
