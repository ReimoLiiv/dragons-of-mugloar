package com.dragonsofmugloar.client;

import com.dragonsofmugloar.client.exception.MugloarServerException;
import com.dragonsofmugloar.model.responses.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Retryable(
        retryFor = {MugloarServerException.class, ResourceAccessException.class},
        backoff = @Backoff(delay = 500, multiplier = 2.0)
)
@Component
public class MugloarApiClient {

    public static final String GAME_ID = "gameId";
    public static final String ITEM_ID = "itemId";
    public static final String AD_ID = "adId";

    private final RestClient restClient;

    public MugloarApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public GameStartResponse startGame() {
            return restClient.post()
                    .uri(ApiEndpoints.START_GAME.path())
                    .retrieve()
                    .body(GameStartResponse.class);
    }

    public ReputationResponse investigateReputation(String gameId) {
        return restClient.post().uri(ApiEndpoints.INVESTIGATE_REPUTATION.path(), Map.of(GAME_ID, gameId)
                )
                .retrieve()
                .body(ReputationResponse.class);
    }

    public List<MessageTask> getMessages(String gameId) {
        return restClient.get()
                .uri(ApiEndpoints.GET_MESSAGES.path(), Map.of(GAME_ID, gameId))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public SolveResponse solveMessage(String gameId, String adId) {
        return restClient.post()
                .uri(ApiEndpoints.SOLVE_MESSAGE.path(), Map.of(GAME_ID, gameId, AD_ID, adId))
                .retrieve()
                .body(SolveResponse.class);
    }

    public List<ShopItem> getShopItems(String gameId) {
        return restClient.get()
                .uri(ApiEndpoints.GET_SHOP_ITEMS.path(), Map.of(GAME_ID, gameId))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public BuyItemResponse buyItem(String gameId, String itemId) {
        return restClient.post().uri(ApiEndpoints.BUY_SHOP_ITEM.path(), Map.of(GAME_ID, gameId, ITEM_ID, itemId)
                )
                .retrieve()
                .body(BuyItemResponse.class);
    }
}
