package com.dragonsofmugloar.strategy;

import com.dragonsofmugloar.model.responses.MessageTask;
import com.dragonsofmugloar.model.responses.ReputationResponse;
import com.dragonsofmugloar.model.strategy.Probability;
import com.dragonsofmugloar.model.strategy.ReputationRules;
import com.dragonsofmugloar.model.strategy.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class Strategy {

    private final StrategyProperties props;

    public Strategy(StrategyProperties props) {
        this.props = props;
    }

    public Optional<MessageTask> chooseMessage(List<MessageTask> messages, ReputationResponse reputation) {
        return messages.stream()
                .filter(this::isAllowedMessage)
                .filter(task -> isAllowedByReputation(task, reputation))
                .flatMap(task -> toWeighted(task).stream())
                .max(Comparator.comparingDouble(this::expectedScore))
                .map(Map.Entry::getKey);
    }

    private Optional<Map.Entry<MessageTask, Probability>> toWeighted(MessageTask task) {
        return Probability.fromApi(task.probability())
                .filter(props.probabilityWeights()::containsKey)
                .map(p -> Map.entry(task, p));
    }

    private double expectedScore(Map.Entry<MessageTask, Probability> entry) {
        return entry.getKey().reward() * props.probabilityWeights().get(entry.getValue());
    }

    public Optional<String> decideShopPurchase(int gold, int lives, int healingPotionsBought) {
        Shop shop = props.shop();

        if (shouldEmergencyHeal(shop, gold, lives, healingPotionsBought)) {
            return Optional.of("hpot");
        }

        if (canBuyAdvanced(shop, gold)) {
            return Optional.of(randomItem(shop.itemGroups().advanced()));
        }

        if (canBuyBasic(shop, gold)) {
            return Optional.of(randomItem(shop.itemGroups().basic()));
        }

        return Optional.empty();
    }

    private boolean isAllowedByReputation(MessageTask task, ReputationResponse reputation) {
        ReputationRules rules = props.reputationRules();

        if (!rules.enabled()) {
            return true;
        }

        String msg = task.message().toLowerCase();
        if (!msg.contains("people")) {
            return true;
        }

        int people = reputation.people();
        int statePenalty = Math.abs(reputation.state());
        int deficit = statePenalty - people;
        return deficit <= rules.maxStateDeficit();
    }

    private boolean shouldEmergencyHeal(Shop shop, int gold, int lives, int healingPotionsBought) {
        return shop.enabled().healingPotion()
                && lives < shop.emergencyHealLivesBelow()
                && healingPotionsBought < shop.limits().maxHealingPotions()
                && gold >= 50;
    }

    private boolean canBuyAdvanced(Shop shop, int gold) {
        return shop.enabled().advancedItems()
                && gold >= shop.buyThresholds().advancedItemsGold();
    }

    private boolean canBuyBasic(Shop shop, int gold) {
        return shop.enabled().basicItems()
                && gold >= shop.buyThresholds().basicItemsGold();
    }

    private String randomItem(List<String> items) {
        return items.get(ThreadLocalRandom.current().nextInt(items.size()));
    }

    private boolean isAllowedMessage(MessageTask task) {
        String msg = task.message().toLowerCase();

        return props.messageFilters()
                .forbiddenPhrases()
                .stream()
                .map(String::toLowerCase)
                .noneMatch(msg::contains);
    }
}
