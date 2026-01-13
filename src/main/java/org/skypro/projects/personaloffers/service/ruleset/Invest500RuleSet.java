package org.skypro.projects.personaloffers.service.ruleset;

import org.skypro.projects.personaloffers.model.Product;
import org.skypro.projects.personaloffers.repository.ProductExternalRepository;
import org.skypro.projects.personaloffers.service.RecommendationRuleSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500RuleSet implements RecommendationRuleSet {

    @Autowired
    private ProductExternalRepository productRepository;

    @Override
    public Optional<Product> applyRules(UUID userId) {

        // Check if user uses at least one DEBIT type product
        List<Product> debitProducts = productRepository.findProductsByUserIdAndType(userId, "DEBIT");
        if (debitProducts.isEmpty()) {
            return Optional.empty();
        }

        // Check if user doesn't use any INVEST type products
        List<Product> investProducts = productRepository.findProductsByUserIdAndType(userId, "INVEST");
        if (!investProducts.isEmpty()) {
            return Optional.empty();
        }

        // Check if the sum of top-up transactions for SAVING type products is greater than 1000 rubles
        double topUpAmount = productRepository.getTopUpAmountForSavingProducts(userId);
        if (topUpAmount <= 1000.0) {
            return Optional.empty();
        }

        return Optional.of(this.getProduct());
    }

    private Product getProduct() {
        Product offer = new Product();
        offer.setId(UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"));
        offer.setName("Invest 500");
        offer.setDescription("Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!");

        return offer;
    }
}
