package uz.ikhtidev.bukharatelefonbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.ikhtidev.bukharatelefonbot.domain.Phone;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    List<Phone> findByOwnerIdAndIsActive(Long userId, Boolean isActive);

//    @Query("SELECT p FROM Phone p WHERE MONTH(p.postedAt) = :month")
//    List<Phone> findByPostedAtMonth(int month);

    List<Phone> findByPostedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
