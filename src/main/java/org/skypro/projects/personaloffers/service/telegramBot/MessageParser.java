package org.skypro.projects.personaloffers.service.telegramBot;

import org.skypro.projects.personaloffers.exception.WrongMessageParserException;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MessageParser {

    private static final Pattern RECOMMEND_PATTERN = Pattern.compile("^\\/recommend\\s+([a-zA-Z0-9.]{1,255})$");

    public String parseRecommend(String input) {
        Matcher matcher = RECOMMEND_PATTERN.matcher(input.trim());

        if (!matcher.find()) {
            throw new WrongMessageParserException("Wrong message format: '" + input + "'");
        }

        return toLowerCase(matcher.group(1));
    }

    private String toLowerCase(String str) {
        return str.toLowerCase();
    }
}
