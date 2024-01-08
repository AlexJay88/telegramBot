package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@EnableScheduling
public class  TelegramBotUpdatesListener implements UpdatesListener {


    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository repository;




    private final static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final static Pattern MESSAGE_PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        logger.info("Processing update: {}", updates);
        for (Update update : updates) {
            String messageText = update.message().text();
            long chatId = update.message().chat().id();
            try {
                switch (messageText) {
                    case "/start":
                        startCommandReceived(chatId, update.message().chat().firstName());
                        logger.info("Приветственное сообщение было отправлено");
                        break;
                    default:
                        sendReminder(chatId, messageText);
                        logger.info("Напоминание сохранено");
                        break;
                }
            } catch (NullPointerException e) {
                sendMessage(chatId, "Ошибка формата сообщения");
                logger.info("Ошибка формата сообщения");
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Привет, " + name + ", напиши напоминание в формате : dd.mm.yyyy HH:MM текст напоминания";
        sendMessage(chatId, answer);
    }


    private void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage(chatId, textToSend);
        SendResponse response = telegramBot.execute(sendMessage);
        if (!response.isOk()) {
            logger.error("Произошла ошибка при отправке сообщения боту: " + response.description());
        }
    }

    public void sendReminder(long chatId, String text) {
        Matcher matcher = MESSAGE_PATTERN.matcher(text);
        try {
            if (matcher.matches()) {
                String task = matcher.group(3);
                LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(1), DATE_TIME_FORMAT);
                repository.save(new NotificationTask(chatId, task, localDateTime));
                sendMessage(chatId, "Напоминание сохранено!");
            } else {
                sendMessage(chatId, "Неправильный формат, попробуй dd.mm.yyyy HH:MM текст ");
            }
        } catch (DateTimeParseException e) {
            sendMessage(chatId, "Ошибка даты, попробуй снова");
            logger.info("Ошибка даты");
        }
    }

    public NotificationTask findTask() {
        NotificationTask task = repository.findByTimeReminder(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        logger.info("Задача запущена");
        return task;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        findTask();
    }
}
