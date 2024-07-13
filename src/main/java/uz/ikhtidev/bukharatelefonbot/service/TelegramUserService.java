package uz.ikhtidev.bukharatelefonbot.service;

import org.springframework.stereotype.Service;
import uz.ikhtidev.bukharatelefonbot.domain.TelegramUser;
import uz.ikhtidev.bukharatelefonbot.repository.TelegramUserRepository;

@Service
public class TelegramUserService {
    private final TelegramUserRepository userRepository;

    public TelegramUserService(TelegramUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public TelegramUser saveUser(TelegramUser user) {
        return userRepository.save(user);
    }

    public TelegramUser getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}