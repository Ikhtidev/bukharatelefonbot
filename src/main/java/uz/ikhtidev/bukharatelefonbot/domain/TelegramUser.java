package uz.ikhtidev.bukharatelefonbot.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
@Table(name = "telegram_user")
public class TelegramUser implements Serializable {
    @Id
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "user_step")
    private String step;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "user_name")
    private String userName;
}
