package com.dragonsofmugloar.client;

public enum ApiEndpoint {

    START_GAME("/game/start"),
    INVESTIGATE_REPUTATION("/{gameId}/investigate/reputation"),
    GET_MESSAGES("/{gameId}/messages"),
    SOLVE_MESSAGE("/{gameId}/solve/{adId}"),
    GET_SHOP_ITEMS("/{gameId}/shop"),
    BUY_SHOP_ITEM("/{gameId}/shop/buy/{itemId}");

    private final String path;

    ApiEndpoint(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
