package uz.ikhtidev.bukharatelefonbot.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@Component
public class MessageUtils {
    public SendMessage generateSendMessageWithText(Long chatId, String text){
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        return sendMessage;
    }

    public SendPhoto generateSendPhotoWithCaption(Long chatId, String photoFileId, String caption){
        var sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(photoFileId));
        sendPhoto.setCaption(caption);
        return sendPhoto;
    }

    public SendPhoto generatePostForChannel(String chatId, String photoFileId, String caption){
        var sendPhoto = new SendPhoto();
        sendPhoto.setChatId("@" + chatId);
        sendPhoto.setPhoto(new InputFile(photoFileId));
        sendPhoto.setCaption(caption);
        return sendPhoto;
    }
}