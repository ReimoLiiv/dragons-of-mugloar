package com.dragonsofmugloar.client;

import com.dragonsofmugloar.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

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
                .uri(ApiEndpoint.START_GAME.path())
                .retrieve()
                .body(GameStartResponse.class);
    }

    public ReputationResponse investigateReputation(String gameId) {
        return restClient.post().uri(ApiEndpoint.INVESTIGATE_REPUTATION.path(), Map.of(GAME_ID, gameId)
                )
                .retrieve()
                .body(ReputationResponse.class);
    }

    public List<MessageTask> getMessages(String gameId) {
        return restClient.get()
                .uri(ApiEndpoint.GET_MESSAGES.path(), Map.of(GAME_ID, gameId))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public SolveResponse solveMessage(String gameId, String adId) {
        return restClient.post()
                .uri(ApiEndpoint.SOLVE_MESSAGE.path(), Map.of(GAME_ID, gameId, AD_ID, adId))
                .retrieve()
                .body(SolveResponse.class);
    }

    public List<ShopItem> getShopItems(String gameId) {
        return restClient.get()
                .uri(ApiEndpoint.GET_SHOP_ITEMS.path(), Map.of(GAME_ID, gameId))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public BuyItemResponse buyItem(String gameId, String itemId) {
        return restClient.post().uri(ApiEndpoint.BUY_SHOP_ITEM.path(), Map.of(GAME_ID, gameId, ITEM_ID, itemId)
                )
                .retrieve()
                .body(BuyItemResponse.class);
    }
}
