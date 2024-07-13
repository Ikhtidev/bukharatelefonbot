package uz.ikhtidev.bukharatelefonbot.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.ikhtidev.bukharatelefonbot.utils.BotConfig;

@SuppressWarnings("deprecation")
@Component
@Log4j
public class BukharaTelefonBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final UpdateController updateController;


    @Autowired
    public BukharaTelefonBot(final BotConfig config, UpdateController updateController) {
        this.config = config;
        this.updateController = updateController;
    }

    @PostConstruct
    public void init() {
        updateController.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            message.enableHtml(true);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    public String sendAnswerMessage(SendPhoto sendPhoto) {
        String postLink = "";
        if (sendPhoto != null) {
            try {
                sendPhoto.setParseMode(ParseMode.HTML);
                Message execute = execute(sendPhoto);
                int messageId = execute.getMessageId();
                postLink = "https://t.me/" + getBotChannelUsername() + "/" + messageId;
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
        return postLink;
    }

    public void sendEditMessageReplyMarkup(EditMessageReplyMarkup editMessageReplyMarkup) {
        if (editMessageReplyMarkup != null) {
            try {
                execute(editMessageReplyMarkup);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    public Long getBotModeratorId() {
        return config.getBotModerator();
    }

    public String getBotChannelUsername() {
        return config.getBotChannel();
    }
}
