package org.skypro.projects.personaloffers.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import org.skypro.projects.personaloffers.service.telegramBot.BotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final BotService botService;

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(BotService botService) {
        this.botService = botService;
    }

    @PostConstruct
    public void init() {
        this.telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: '{}'", update);

            if (update.message() != null && update.message().text() != null) {
                if (update.message().text().startsWith("/recommend")) {
                    try {
                        telegramBot.execute(
                                new com.pengrad.telegrambot.request.SendMessage(
                                        update.message().chat().id(),
                                        botService.getRecommendations(update.message().text())
                                )
                        );
                    } catch (IllegalArgumentException e) {
                        telegramBot.execute(
                                new com.pengrad.telegrambot.request.SendMessage(
                                        update.message().chat().id(),
                                        e.getMessage()
                                )
                        );
                        this.sendStartMessage(update);
                    }
                } else {
                    this.sendStartMessage(update);
                }
            }
        });

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendStartMessage(Update update) {
        telegramBot.execute(
                new com.pengrad.telegrambot.request.SendMessage(
                        update.message().chat().id(),
                        "Здравствуйте, " + update.message().chat().firstName() + "!"
                )
        );
        telegramBot.execute(
                new com.pengrad.telegrambot.request.SendMessage(
                        update.message().chat().id(),
                        "Вы можете получить рекомендуемые продукты набрав команду: \n\"/recommend [username]\""
                )
        );
    }
}
