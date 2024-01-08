package pro.sky.telegrambot.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long chatId;
    private String textMessage;
    private LocalDateTime timeReminder;

    public NotificationTask(long chatId, String textMessage, LocalDateTime timeReminder) {
        this.chatId = chatId;
        this.textMessage = textMessage;
        this.timeReminder = timeReminder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return id == that.id && chatId == that.chatId && Objects.equals(textMessage, that.textMessage) && Objects.equals(timeReminder, that.timeReminder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, textMessage, timeReminder);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public LocalDateTime getTimeReminder() {
        return timeReminder;
    }

    public void setTimeReminder(LocalDateTime timeReminder) {
        this.timeReminder = timeReminder;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", textMessage='" + textMessage + '\'' +
                ", timeReminder=" + timeReminder +
                '}';

    }
}

