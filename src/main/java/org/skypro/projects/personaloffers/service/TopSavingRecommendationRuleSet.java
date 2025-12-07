package org.skypro.projects.personaloffers.service;

import org.skypro.projects.personaloffers.model.Offer;
import org.skypro.projects.personaloffers.model.Product;
import org.skypro.projects.personaloffers.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRecommendationRuleSet implements RecommendationRuleSet {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Optional<Offer> applyRules(UUID userId) {

        // Check if user uses at least one DEBIT type product
        List<Product> debitProducts = productRepository.findProductsByUserIdAndType(userId, "DEBIT");
        if (debitProducts.isEmpty()) {
            return Optional.empty();
        }

        // Get the sum of top-up transactions for DEBIT type products
        double debitTopUpAmount = productRepository.getTopUpAmountForDebitProducts(userId);

        // Get the sum of top-up transactions for SAVING type products
        double savingTopUpAmount = productRepository.getTopUpAmountForSavingProducts(userId);

        // Check nested condition: sum of DEBIT top-ups >= 50000 OR sum of SAVING top-ups >= 50000
        boolean nestedCondition = debitTopUpAmount >= 50000.0 || savingTopUpAmount >= 50000.0;
        if (!nestedCondition) {
            return Optional.empty();
        }

        // Get the sum of expenses for DEBIT type products
        double debitExpenseAmount = productRepository.getExpenseAmountForDebitProducts(userId);

        // Check if the sum of top-ups for DEBIT is greater than the sum of expenses for DEBIT
        if (debitTopUpAmount <= debitExpenseAmount) {
            return Optional.empty();
        }

        Offer offer = new Offer();
        offer.setId(UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"));
        offer.setName("Top Saving");
        offer.setDescription("Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\n" +
                "\n" +
                "Преимущества «Копилки»:\n" +
                "\n" +
                "Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.\n" +
                "\n" +
                "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.\n" +
                "\n" +
                "Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.\n" +
                "\n" +
                "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!");

        return Optional.of(offer);
    }
}
