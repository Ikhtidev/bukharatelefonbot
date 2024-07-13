package uz.ikhtidev.bukharatelefonbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ikhtidev.bukharatelefonbot.domain.TelegramUser;

//@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
}