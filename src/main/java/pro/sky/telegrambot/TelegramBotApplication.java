package pro.sky.telegrambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramBotApplication {
	private static final Logger logger = LoggerFactory.getLogger(TelegramBotApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
		logger.info("Бот стартовал");
	}

}
