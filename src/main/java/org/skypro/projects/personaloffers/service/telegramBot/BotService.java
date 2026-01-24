package org.skypro.projects.personaloffers.service.telegramBot;

import org.skypro.projects.personaloffers.exception.TooMuchUsersException;
import org.skypro.projects.personaloffers.exception.UserNotFoundException;
import org.skypro.projects.personaloffers.exception.WrongMessageParserException;
import org.skypro.projects.personaloffers.model.Product;
import org.skypro.projects.personaloffers.model.User;
import org.skypro.projects.personaloffers.service.OffersService;
import org.skypro.projects.personaloffers.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotService {

    private final OffersService offersService;
    private final Logger logger = LoggerFactory.getLogger(BotService.class);
    private final MessageParser messageParser;
    private final UserService userService;

    public BotService(
            MessageParser messageParser,
            OffersService offersService,
            UserService userService
    ) {
        this.messageParser = messageParser;
        this.offersService = offersService;
        this.userService = userService;
    }

    public String getRecommendations(String message) {
        try {
            String userName = messageParser.parseRecommend(message);

            if (userName.isEmpty()) {
                this.logger.debug("Username is empty from message: {}", message);

                return "Получен пустой username пользователя. Повторите запрос с правильным форматом: /recommend username";
            }

            User user = this.userService.getOneUserByUserNameOrFail(userName);

            return this.convertRecommendationsToString(this.offersService.getRecommendations(user.getId()));
        } catch (UserNotFoundException | TooMuchUsersException e) {
            String messageText = "Пользователь не найден";
            this.logger.debug(messageText);

            return messageText;

        } catch (WrongMessageParserException e) {
            this.logger.warn("Invalid message format: '{}'. Exception: {}", message, e.getMessage());

            return "Неправильный формат запроса. Повторите запрос с правильным форматом: /recommend username";
        } catch (Throwable e) {
            String messageText = "Что-то пошло не так";
            this.logger.error(messageText, e);

            return messageText;
        }
    }

    private String convertRecommendationsToString(List<Product> recommendations) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Новые продукты для вас:\n");

        for (Product recommendation : recommendations) {
            if (recommendation != null) {
                stringBuilder.append(recommendation.getName()).append("\n");
            }
        }

        return stringBuilder.toString();
    }
}
