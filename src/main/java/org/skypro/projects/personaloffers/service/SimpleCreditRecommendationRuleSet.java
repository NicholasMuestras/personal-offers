package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.model.Product;
import org.skypro.projects.personaloffers.repository.ProductExternalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRecommendationRuleSet implements RecommendationRuleSet {

    @Autowired
    private ProductExternalRepository productRepository;

    @Override
    public Optional<Product> applyRules(UUID userId) {

        // Check if user doesn't use any CREDIT type products
        List<Product> creditProducts = productRepository.findProductsByUserIdAndType(userId, "CREDIT");
        if (!creditProducts.isEmpty()) {
            return Optional.empty();
        }

        // Get the sum of top-up transactions for DEBIT type products
        double debitTopUpAmount = productRepository.getTopUpAmountForDebitProducts(userId);

        // Get the sum of expenses for DEBIT type products
        double debitExpenseAmount = productRepository.getExpenseAmountForDebitProducts(userId);

        // Check if the sum of top-ups for DEBIT is greater than the sum of expenses for DEBIT
        if (debitTopUpAmount <= debitExpenseAmount) {
            return Optional.empty();
        }

        // Check if the sum of expenses for DEBIT type products is greater than 100000 rubles
        if (debitExpenseAmount <= 100000.0) {
            return Optional.empty();
        }

        Product offer = new Product();
        offer.setId(UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"));
        offer.setName("Простой кредит");
        offer.setDescription("Откройте мир выгодных кредитов с нами!\n" +
                "\n" +
                "Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\n" +
                "\n" +
                "Почему выбирают нас:\n" +
                "\n" +
                "Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\n" +
                "\n" +
                "Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\n" +
                "\n" +
                "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.\n" +
                "\n" +
                "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!");

        return Optional.of(offer);
    }
}
