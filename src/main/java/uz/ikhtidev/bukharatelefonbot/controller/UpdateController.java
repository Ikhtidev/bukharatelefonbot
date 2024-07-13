package uz.ikhtidev.bukharatelefonbot.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import uz.ikhtidev.bukharatelefonbot.constants.Constants;
import uz.ikhtidev.bukharatelefonbot.constants.TelegramUserStep;
import uz.ikhtidev.bukharatelefonbot.domain.Phone;
import uz.ikhtidev.bukharatelefonbot.domain.TelegramUser;
import uz.ikhtidev.bukharatelefonbot.service.PhoneService;
import uz.ikhtidev.bukharatelefonbot.service.TelegramUserService;
import uz.ikhtidev.bukharatelefonbot.utils.MessageUtils;
import uz.ikhtidev.bukharatelefonbot.utils.ReplyMarkupUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Log4j
public class UpdateController {

    private BukharaTelefonBot telegramBot;
    private final MessageUtils messageUtils;
    private final TelegramUserService userService;
    private final PhoneService phoneService;

    public UpdateController(MessageUtils messageUtils, TelegramUserService userService, PhoneService phoneService) {
        this.messageUtils = messageUtils;
        this.userService = userService;
        this.phoneService = phoneService;
    }

    public void registerBot(BukharaTelefonBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }
        if (update.hasMessage()) {
            distributeMessagesByType(update);
        } else if (update.hasCallbackQuery()) {
            processCallbacks(update);
        } else {
            log.error("Unsupported message type is received: " + update);
        }
    }

    private void distributeMessagesByType(Update update) {
        Message message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        } else if (message.hasPhoto()) {
            processPhotoMessage(update);
        } else if (message.hasContact()) {
            processContactMessage(update);
        } else {
            processUnsupportedMessage(update);
        }
    }

    private void processTextMessage(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        TelegramUser user = getUser(message.getChatId());
        if (text.equals(TelegramUserStep.START)) {
            if (Objects.equals(user.getChatId(), telegramBot.getBotModeratorId())) {
                user.setFullName(message.getFrom().getFirstName());
                user.setUserName(message.getFrom().getUserName());
                var sendMessage = messageUtils.generateSendMessageWithText(message.getChatId(),
                        "Assalomu aleykum @" + telegramBot.getBotChannelUsername() + " kanalining admini bo'lmish <b>" + user.getFullName() + "</b>!\n\n" +
                                "Barcha buyruqlar pastda \uD83D\uDC47\uD83D\uDC47\uD83D\uDC47");
                sendMessage.setReplyMarkup(ReplyMarkupUtil.createAdminCommandsMarkup());
                setView(sendMessage);
                user.setStep(TelegramUserStep.ADMIN_FULL_ACCESS);
                userService.saveUser(user);
            } else if (!Objects.equals(user.getStep(), TelegramUserStep.WAITING_MODERATOR)) {
                user.setFullName(message.getFrom().getFirstName());
                user.setUserName(message.getFrom().getUserName());
                var sendMessage = messageUtils.generateSendMessageWithText(message.getChatId(),
                        "Assalomu aleykum " + user.getFullName() + "!\n\n" +
                                "\uD83C\uDFEA Buxoro bo'yicha eng katta va eng ommabop telefon bozoriga xush kelibsiz!\n\n" +
                                "Bizning kanal: @" + telegramBot.getBotChannelUsername() + "\n\n" +
                                "Quyida siz telefoningizni sotish uchun kanalimizga reklama joylashtirishingiz yoki " +
                                "admin bilan bog'lanishingiz mumkin,\n" +
                                "Xizmat turini tanlang: ");
                TelegramUser botModerator = getUser(telegramBot.getBotModeratorId());
                sendMessage.setReplyMarkup(ReplyMarkupUtil.createOurServicesMarkup(botModerator.getUserName()));
                setView(sendMessage);
                user.setStep(TelegramUserStep.SELECT_CREATE_POST_OR_ADMIN);
                userService.saveUser(user);
            }
        }
        else if (user.getStep().equals(TelegramUserStep.ENTER_PHONE_NAME)) {
            Phone phone = getPhone(user.getChatId());
            phone.setName(text);
            phone.setOwnerUsername(user.getUserName());
            phoneService.savePhone(phone);
            var sendMessage = messageUtils.generateSendMessageWithText(message.getChatId(), "<b>Yaxshi!</b>\n" +
                    "✅ Xotirasi (<i>Misol: 32 Gb</i>):");
            setView(sendMessage);
            user.setStep(TelegramUserStep.ENTER_PHONE_MEMORY);
            userService.saveUser(user);
        }
        else if (user.getStep().equals(TelegramUserStep.ENTER_PHONE_MEMORY)) {
            Phone phone = getPhone(user.getChatId());
            phone.setMemory(text.toLowerCase());
            phoneService.savePhone(phone);
            var sendMessage = messageUtils.generateSendMessageWithText(message.getChatId(), "<b>Yaxshi!</b>\n" +
                    "\uD83D\uDCC5 Yili (<i>Misol: 2020</i>):");
            setView(sendMessage);
            user.setStep(TelegramUserStep.ENTER_PHONE_YEAR);
            userService.saveUser(user);
        }
        else if (user.getStep().equals(TelegramUserStep.ENTER_PHONE_YEAR)) {
            Phone phone = getPhone(user.getChatId());
            phone.setYear(text.toLowerCase());
            phoneService.savePhone(phone);
            var sendMessage = messageUtils.generateSendMessageWithText(message.getChatId(), "<b>Zo'r!</b>\n" +
                    "⚪️ Rangi (<i>Misol: Oq</i>):");
            setView(sendMessage);
            user.setStep(TelegramUserStep.ENTER_PHONE_COLOR);
            userService.saveUser(user);
        }
        else if (user.getStep().equals(TelegramUserStep.ENTER_PHONE_COLOR)) {
            Phone phone = getPhone(user.getChatId());
            phone.setColor(text.toLowerCase());
            phoneService.savePhone(phone);
            var sendMessage = messageUtils.generateSendMessageWithText(message.getChatId(), "<b>Zo'r!</b>\n" +
                    "\uD83D\uDCE6 Dokument - karopka");
            sendMessage.setReplyMarkup(ReplyMarkupUtil.createPhoneDocumentMarkup());
            setView(sendMessage);
            user.setStep(TelegramUserStep.ENTER_PHONE_HAS_DOC);
            userService.saveUser(user);
        }
        else if (user.getStep().equals(TelegramUserStep.ENTER_PHONE_CONDITION)) {
            Phone phone = getPhone(user.getChatId());
            phone.setCondition(text.toLowerCase());
            phoneService.savePhone(phone);
            var sendMessage = messageUtils.generateSendMessageWithText(message.getChatId(), "<b>Yaxshi!</b>\n" +
                    "\uD83D\uDD0B Zaryadi (<i>Misol: 1 kunga yetadi yoki 100%</i>)");
            setView(sendMessage);
            user.setStep(TelegramUserStep.ENTER_PHONE_CHARGE);
            userService.saveUser(user);
        }
        else if (user.getStep().equals(TelegramUserStep.ENTER_PHONE_CHARGE)) {
            Phone phone = getPhone(user.getChatId());
            phone.setCharge(text.toLowerCase());
            phoneService.savePhone(phone);
            var sendMessage = messageUtils.generateSendMessageWithText(message.getChatId(), "<b>Ajoyib!</b>\n" +
                    "\uD83D\uDD01 Obmen");
            sendMessage.setReplyMarkup(ReplyMarkupUtil.createReplaceMarkup());
            setView(sendMessage);
            user.setStep(TelegramUserStep.ENTER_PHONE_IS_REPLACE);
            userService.saveUser(user);
        }
        else if (user.getStep().equals(TelegramUserStep.ENTER_PHONE_PRICE)) {
            Phone phone = getPhone(user.getChatId());
            phone.setPrice(text.toLowerCase());
            phoneService.savePhone(phone);
            var sendMessage = messageUtils.generateSendMessageWithText(message.getChatId(), "<b>Yaxshi!</b>\n" +
                    "\uD83D\uDCCD Manzil (<i>Misol: Shofirkon tumani</i>):");
            setView(sendMessage);
            user.setStep(TelegramUserStep.ENTER_PHONE_ADDRESS);
            userService.saveUser(user);
        }
        else if (user.getStep().equals(TelegramUserStep.ENTER_PHONE_ADDRESS)) {
            Phone phone = getPhone(user.getChatId());
            phone.setAddress(text);
            phoneService.savePhone(phone);
            var sendMessage = messageUtils.generateSendMessageWithText(message.getChatId(), "<b>Davom etamiz!</b>\n" +
                    "\uD83D\uDCDE Xaridorlar uchun quyidagi tugma orqali kontaktingizni ulashing \uD83D\uDC47");
            sendMessage.setReplyMarkup(null);
            sendMessage.setReplyMarkup(ReplyMarkupUtil.createUserContactMarkup());
            setView(sendMessage);
            user.setStep(TelegramUserStep.ENTER_OWNER_PHONE_NUMBER);
            userService.saveUser(user);
        }
        else if (user.getStep().equals(TelegramUserStep.ADMIN_FULL_ACCESS)) {
            if (text.equals("\uD83D\uDDD3 Oylik hisobot")) {
                List<Phone> phonesForCurrentMonth = phoneService.getPhonesForCurrentMonth();
                String report = createReport(phonesForCurrentMonth);
                var sendMessage = messageUtils.generateSendMessageWithText(telegramBot.getBotModeratorId(), report);
                setView(sendMessage);
            }
            else if (text.equals("\uD83D\uDCCA Kanal statistikasi")) {
                String caption = "<b>" + user.getFullName() + "</b> akajon!\nKanalimizning statistikasini quyidagi link orqali ko'rishingiz mumkin:\n\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47\nhttps://uz.tgstat.com/channel/@BuxorodaTelefonBozor/stat";
                var sendMessage = messageUtils.generateSendMessageWithText(telegramBot.getBotModeratorId(), caption);
                setView(sendMessage);
            }
        }

    }

    private String createReport(List<Phone> phones) {

        StringBuilder report = new StringBuilder();
        if (phones.isEmpty()) {
            report.append("<b>Hali bu oyda kanalga postlar joylanmagan!</b>");
        } else {
            Map<LocalDate, List<Phone>> monthlyReport = phones.stream()
                    .collect(Collectors.groupingBy(phone -> phone.getPostedAt().toLocalDate()));

            report.append("<b>Hisobot varaqasi ").append(LocalDateTime.now().getMonth()).append(".</b>\n\n");
            monthlyReport.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        LocalDate date = entry.getKey();
                        List<Phone> phonesForDate = entry.getValue();

                        report.append("<b>").append(date).append(":</b> ").append(phonesForDate.size()).append(" ta post (");
                        report.append(phonesForDate.stream().map(Phone::getName).collect(Collectors.joining(", "))).append(")\n");
                    });
            report.append("\n<b>Jami shu oyda ").append(phones.size()).append(" ta post joylangan!</b>");
        }
        return report.toString();
    }

    private void processCallbacks(Update update) {
        String callData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getFrom().getId();
        TelegramUser user = getUser(chatId);
        switch (user.getStep()) {
            case TelegramUserStep.SELECT_CREATE_POST_OR_ADMIN -> {
                if (callData.equals(Constants.ORDER_SIMPLE)) {
                    var sendMessage = messageUtils.generateSendMessageWithText(chatId, """
                            <b>Yaxshi!</b>
                            Endi sotmoqchi bo'lgan telefoningiz ma'lumotlarini birma-bir kiritasiz.

                            \uD83D\uDCF1 Telefon nomi (<i>Misol: iPhone 7</i>):""");
                    setView(sendMessage);
                    user.setStep(TelegramUserStep.ENTER_PHONE_NAME);
                    userService.saveUser(user);
                }
            }
            case TelegramUserStep.ENTER_PHONE_HAS_DOC -> {
                Phone phone = getPhone(user.getChatId());
                if (callData.equals(Constants.DOCUMENT_HAS)) {
                    phone.setHasDocument(true);
                } else if (callData.equals(Constants.DOCUMENT_NOT)) {
                    phone.setHasDocument(false);
                }
                phoneService.savePhone(phone);
                var sendMessage = messageUtils.generateSendMessageWithText(chatId, "<b>Yaxshi!</b>\n" +
                        "\uD83D\uDEE0 Holati (<i>Misol: yangi</i>):");
                setView(sendMessage);
                user.setStep(TelegramUserStep.ENTER_PHONE_CONDITION);
                userService.saveUser(user);
            }
            case TelegramUserStep.ENTER_PHONE_IS_REPLACE -> {
                Phone phone = getPhone(user.getChatId());
                if (callData.equals(Constants.REPLACE_HAS)) {
                    phone.setIsReplace(true);
                } else if (callData.equals(Constants.REPLACE_NOT)) {
                    phone.setIsReplace(false);
                }
                phoneService.savePhone(phone);
                var sendMessage = messageUtils.generateSendMessageWithText(chatId, "<b>Yaxshi!</b>\n" +
                        "\uD83D\uDCB0 Narxi:");
                setView(sendMessage);
                user.setStep(TelegramUserStep.ENTER_PHONE_PRICE);
                userService.saveUser(user);
            }
            case TelegramUserStep.SEND_POST_TO_ADMIN_FOR_PUBLISH -> {
                if (callData.equals(Constants.SEND_POST_TO_ADMIN)) {
                    Phone phone = getPhone(user.getChatId());
                    sendPostToAdminForPublish(phone);
                    var sendMessage = messageUtils.generateSendMessageWithText(chatId, "<b>✅</b>" +
                            "Sizning e'loningiz qabul qilindi va moderatorga jo'natildi, tekshiruvdan so'ng kanalimizda e'lon qilinadi. E'lon kanalga qo'yilganida sizga xabar beramiz.\n\uD83D\uDC49 @" + telegramBot.getBotUsername());
                    setView(sendMessage);
                    user.setStep(TelegramUserStep.WAITING_MODERATOR);
                    userService.saveUser(user);
                }
            }
            case TelegramUserStep.ADMIN_FULL_ACCESS -> {
                String[] commands = callData.split(" "); // phoneId , Post or Delete
                String phoneId = commands[0];
                String command = commands[1];
                Phone phone = phoneService.getPhoneById(Long.parseLong(phoneId));
                TelegramUser phoneOwner = userService.getUserById(phone.getOwnerId());
                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
                editMessageReplyMarkup.setChatId(chatId);
                editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageReplyMarkup.setReplyMarkup(null);
                setEditMessageReplyMarkupView(editMessageReplyMarkup);
                if (command.equals(Constants.ADMIN_PUBLISH_POST)) {
                    phone.setActive(true);
                    phone.setPostedAt(LocalDateTime.now());
                    phoneService.savePhone(phone);

                    String caption = getCaption(phone);
                    var sendPhotoWithCaptionToChannel = messageUtils.generatePostForChannel(telegramBot.getBotChannelUsername(), phone.getImage(), caption);
                    String postLink = setPostView(sendPhotoWithCaptionToChannel);

                    var sendMessageToModerator = messageUtils.generateSendMessageWithText(chatId, "<b>Yaxshi!</b>\n" +
                            "✅[Id=" + phoneId + "] post kanalga joylashtirildi!");
                    setView(sendMessageToModerator);

                    String phoneOwnerMessage = "\uD83C\uDF89<b>Ajoyib!</b>\uD83C\uDF89\nPostingiz kanalga joylashtirildi:\n\n\uD83D\uDC49 " +
                            postLink + "\n\n" +
                            "Yangi post joylash uchun /start ni bosing.";
                    var sendMessageToPhoneOwner = messageUtils.generateSendMessageWithText(phoneOwner.getChatId(), phoneOwnerMessage);

                    setView(sendMessageToPhoneOwner);

                    phoneOwner.setStep(TelegramUserStep.START);
                    userService.saveUser(phoneOwner);
                } else if (command.equals(Constants.ADMIN_DELETE_POST)) {
                    phoneService.deletePhone(phone);

                    var sendMessageToModerator = messageUtils.generateSendMessageWithText(chatId, "<b>Yaxshi!</b>\n" +
                            "❌[Id=" + phoneId + "] post noma'qul deb topildi va bazadan o'chirildi");
                    setView(sendMessageToModerator);

                    String phoneOwnerMessage = "\uD83D\uDE15<b>Afsus!</b>\uD83D\uDE15\nPostingiz moderator tomonidan noma'qul deb topilib, kanalga joylashtirilmadi.\n\nYangi post joylash uchun /start ni bosing.";
                    var sendMessageToPhoneOwner = messageUtils.generateSendMessageWithText(phoneOwner.getChatId(), phoneOwnerMessage);
                    setView(sendMessageToPhoneOwner);

                    phoneOwner.setStep(TelegramUserStep.START);
                    userService.saveUser(phoneOwner);
                }
            }
        }
    }

    private void processContactMessage(Update update) {
        Message message = update.getMessage();
        String phoneNumber = message.getContact().getPhoneNumber().replace("+", "");
        TelegramUser user = getUser(message.getChatId());
        if (user.getStep().equals(TelegramUserStep.ENTER_OWNER_PHONE_NUMBER)) {
            Phone phone = getPhone(user.getChatId());
            phone.setOwnerContact(phoneNumber);
            phoneService.savePhone(phone);
            var sendMessage = messageUtils.generateSendMessageWithText(message.getChatId(), "<b>Oxirgi bosqich!</b>\n" +
                    "\uD83D\uDDBC Telefon rasmini yuboring(faqat bitta rasm):");
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
            keyboardRemove.setRemoveKeyboard(true);
            sendMessage.setReplyMarkup(keyboardRemove);
            setView(sendMessage);
            user.setStep(TelegramUserStep.ENTER_PHONE_IMAGE);
            userService.saveUser(user);
        }
    }

    private void processPhotoMessage(Update update) {
        Message message = update.getMessage();
        TelegramUser user = getUser(message.getChatId());
        if (user.getStep().equals(TelegramUserStep.ENTER_PHONE_IMAGE)) {
            Phone phone = getPhone(user.getChatId());
            PhotoSize photo = message.getPhoto().stream().max(Comparator.comparingInt(PhotoSize::getFileSize)).orElse(null);
            if (photo != null) {
                String fileId = photo.getFileId();
                phone.setImage(fileId);
                phoneService.savePhone(phone);
                String caption = getCaption(phone);
                var sendPhotoWithCaption = messageUtils.generateSendPhotoWithCaption(message.getChatId(), phone.getImage(), caption);
                sendPhotoWithCaption.setReplyMarkup(ReplyMarkupUtil.createUserPostButtonMarkup());
                setPostView(sendPhotoWithCaption);
                user.setStep(TelegramUserStep.SEND_POST_TO_ADMIN_FOR_PUBLISH);
                userService.saveUser(user);
            }

        }
    }

    private void sendPostToAdminForPublish(Phone phone) {
        String caption = getCaption(phone);
        var sendPhotoWithCaption = messageUtils.generateSendPhotoWithCaption(telegramBot.getBotModeratorId(), phone.getImage(), caption);
        sendPhotoWithCaption.setReplyMarkup(ReplyMarkupUtil.createPostOrCancelMarkup(phone.getId()));
        setPostView(sendPhotoWithCaption);
    }

    private String getCaption(Phone phone) {
        StringBuilder caption = new StringBuilder("⚡️⚡️⚡️ <b>E'lon</b> ⚡️⚡️⚡️\n\n")
                .append("\uD83D\uDCF1").append("<b>").append(phone.getName()).append("</b>").append(" sotiladi\n")
                .append("\uD83E\uDDE0<b>Xotirasi:</b> ").append(phone.getMemory()).append("\n")
                .append("\uD83D\uDCC5<b>Yili:</b> ").append(phone.getYear()).append("\n")
                .append("⚪️<b>Rangi:</b> ").append(phone.getColor()).append("\n")
                .append("\uD83D\uDCE6<b>Dokument karobka</b> ").append(phone.getHasDocument() ? "bor\n" : "yo'q\n")
                .append("\uD83D\uDEE0<b>Holati:</b> ").append(phone.getCondition()).append("\n")
                .append("\uD83D\uDD0B<b>Zaryadi:</b> ").append(phone.getCharge()).append("\n")
                .append("\uD83D\uDCB0<b>Narxi:</b> ").append(phone.getPrice()).append("\n")
                .append("\uD83D\uDD04<b>Obmen</b> ").append(phone.getIsReplace() ? "bor\n" : "yo'q\n")
                .append("\uD83D\uDCDE<b>Telefon:</b> +").append(phone.getOwnerContact()).append("\n");
        if (phone.getOwnerUsername() != null)
            caption.append("\uD83D\uDCE5<b>Telegram:</b> @").append(phone.getOwnerUsername()).append("\n");
        caption.append("\uD83D\uDCCD<b>Manzil:</b> ").append(phone.getAddress()).append("\n\n")
                .append("<b>Bizning kanal</b> \uD83D\uDC47\uD83D\uDC47\uD83D\uDC47\n@").append(telegramBot.getBotChannelUsername()).append("\n")
                .append("<b>E'lon berish</b> <i>(bepul)</i> \uD83D\uDC47\uD83D\uDC47\uD83D\uDC47\n@").append(telegramBot.getBotUsername());
        return caption.toString();
    }

    public void setView(SendMessage sendMessage) {
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.enableHtml(true);
        telegramBot.sendAnswerMessage(sendMessage);
    }

    public String setPostView(SendPhoto sendPhoto) {
        return telegramBot.sendAnswerMessage(sendPhoto);
    }

    public void setEditMessageReplyMarkupView(EditMessageReplyMarkup editMessageReplyMarkup) {
        telegramBot.sendEditMessageReplyMarkup(editMessageReplyMarkup);
    }

    private TelegramUser getUser(Long chatId) {
        if (userService.getUserById(chatId) != null) {
            return userService.getUserById(chatId);
        }
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setChatId(chatId);
        telegramUser.setStep(TelegramUserStep.START);
        return userService.saveUser(telegramUser);
    }

    private Phone getPhone(Long userId) {
        List<Phone> inactivePhonesByOwnerId = phoneService.getInactivePhonesByOwnerId(userId);

        if (!inactivePhonesByOwnerId.isEmpty()) {
            return inactivePhonesByOwnerId.get(0);
        }
        Phone phone = new Phone();
        phone.setOwnerId(userId);
        return phoneService.savePhone(phone);
    }

    private void processUnsupportedMessage(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(
                update.getMessage().getChatId(),
                "Qo'llab-quvvatlanmaydigan xabar turi..."
        );
        setView(sendMessage);
        log.error("processUnsupportedMessage() | " + update.getMessage().getFrom().getId() + " | isBot:" + update.getMessage().getFrom().getIsBot());
    }
}