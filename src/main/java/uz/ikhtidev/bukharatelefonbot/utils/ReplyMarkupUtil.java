package uz.ikhtidev.bukharatelefonbot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ReplyMarkupUtil {
    public static InlineKeyboardMarkup createOurServicesMarkup(String moderatorUserName) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> columns = new ArrayList<>();
        InlineKeyboardButton btnUz = InlineKeyboardButton.builder().text("\uD83D\uDCE3 E'lon berish").callbackData(Constants.ORDER_SIMPLE).build();
        InlineKeyboardButton btnRu = InlineKeyboardButton.builder().text("\uD83E\uDD1D Admin bilan bog'lanish").url("https://t.me/"+moderatorUserName).build();
        columns.add(btnUz);
        columns.add(btnRu);
        rows.add(columns);

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public static InlineKeyboardMarkup createPhoneDocumentMarkup() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> columns = new ArrayList<>();
        InlineKeyboardButton btnHas = InlineKeyboardButton.builder().text("✅ Bor").callbackData(Constants.DOCUMENT_HAS).build();
        InlineKeyboardButton btnNo = InlineKeyboardButton.builder().text("❌ Yo'q").callbackData(Constants.DOCUMENT_NOT).build();
        columns.add(btnHas);
        columns.add(btnNo);
        rows.add(columns);

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public static InlineKeyboardMarkup createReplaceMarkup() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> columns = new ArrayList<>();
        InlineKeyboardButton btnHas = InlineKeyboardButton.builder().text("✅ Bor").callbackData(Constants.REPLACE_HAS).build();
        InlineKeyboardButton btnNo = InlineKeyboardButton.builder().text("❌ Yo'q").callbackData(Constants.REPLACE_NOT).build();
        columns.add(btnHas);
        columns.add(btnNo);
        rows.add(columns);

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public static InlineKeyboardMarkup createUserPostButtonMarkup() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> columns = new ArrayList<>();
        InlineKeyboardButton btnPost = InlineKeyboardButton.builder().text("⬆️ E'lonni adminga yuborish").callbackData(Constants.SEND_POST_TO_ADMIN).build();
        columns.add(btnPost);
        rows.add(columns);

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public static InlineKeyboardMarkup createPostOrCancelMarkup(Long phoneId) {
        List<List<InlineKeyboardButton>> row = new ArrayList<>();

        List<InlineKeyboardButton> column1 = new ArrayList<>();
        InlineKeyboardButton btnPost = InlineKeyboardButton.builder().text("✅ Joylash").callbackData(phoneId + " "+Constants.ADMIN_PUBLISH_POST).build();
        InlineKeyboardButton btnAddImage = InlineKeyboardButton.builder().text("❌ O'chirish").callbackData(phoneId + " "+Constants.ADMIN_DELETE_POST).build();
        column1.add(btnPost);
        column1.add(btnAddImage);
        row.add(column1);

        return InlineKeyboardMarkup.builder().keyboard(row).build();
    }

    public static ReplyKeyboardMarkup createUserContactMarkup() {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("\uD83D\uDCDE Kontaktni ulashish");
        keyboardButton.setRequestContact(true);
        keyboardFirstRow.add(keyboardButton);

        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup createAdminCommandsMarkup(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton();
        button1.setText("\uD83D\uDDD3 Oylik hisobot");
        keyboardFirstRow.add(button1);
        KeyboardButton button2 = new KeyboardButton();
        button2.setText("\uD83D\uDCCA Kanal statistikasi");
        keyboardFirstRow.add(button2);

        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
