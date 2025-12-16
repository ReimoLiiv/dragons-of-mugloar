package com.dragonsofmugloar.client;

import com.dragonsofmugloar.client.exception.MugloarServerException;
import com.dragonsofmugloar.model.responses.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(MugloarApiClient.class)
@Import(TestRestClientConfig.class)
@EnableRetry
@TestPropertySource(properties = {"mugloar.base-url=http://localhost",})
class MugloarApiClientTest {

    @Autowired
    MugloarApiClient apiClient;

    @Autowired
    MockRestServiceServer server;

    @Test
    void startGame_success() {
        server.expect(requestTo("http://localhost/game/start"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                          "gameId": "abc123",
                          "lives": 3,
                          "gold": 0,
                          "level": 0,
                          "score": 0,
                          "highScore": 0,
                          "turn": 0
                        }
                        """, MediaType.APPLICATION_JSON));

        GameStartResponse response = apiClient.startGame();

        assertThat(response.gameId()).isEqualTo("abc123");
        assertThat(response.lives()).isEqualTo(3);
        assertThat(response.turn()).isZero();
    }

    @Test
    void getMessages_success() {
        server.expect(requestTo("http://localhost/testGame/messages"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [
                          {
                            "adId": "ad1",
                            "message": "Test message",
                            "reward": 10,
                            "expiresIn": 5,
                            "encrypted": null,
                            "probability": "Walk in the park"
                          }
                        ]
                        """, MediaType.APPLICATION_JSON));

        List<MessageTask> messages = apiClient.getMessages("testGame");

        assertThat(messages).hasSize(1);
        assertThat(messages.getFirst().adId()).isEqualTo("ad1");
        assertThat(messages.getFirst().reward()).isEqualTo(10);
    }

    @Test
    void startGame_retriesOnServerError() {
        server.expect(times(3), requestTo("http://localhost/game/start"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> apiClient.startGame()).isInstanceOf(MugloarServerException.class);

        server.verify();
    }

    @Test
    void investigateReputation_success() {
        server.expect(requestTo("http://localhost/testGame/investigate/reputation"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                          "people": 10,
                          "state": -2,
                          "underworld": 5
                        }
                        """, MediaType.APPLICATION_JSON));

        ReputationResponse response = apiClient.investigateReputation("testGame");

        assertThat(response.people()).isEqualTo(10);
        assertThat(response.state()).isEqualTo(-2);
        assertThat(response.underworld()).isEqualTo(5);
    }

    @Test
    void solveMessage_success() {
        server.expect(requestTo("http://localhost/testGame/solve/ad1"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                          "success": true,
                          "lives": 3,
                          "gold": 30,
                          "score": 100,
                          "highScore": 200,
                          "turn": 5,
                          "message": "Well done"
                        }
                        """, MediaType.APPLICATION_JSON));

        SolveResponse response = apiClient.solveMessage("testGame", "ad1");

        assertThat(response.success()).isTrue();
        assertThat(response.gold()).isEqualTo(30);
        assertThat(response.turn()).isEqualTo(5);
    }

    @Test
    void getShopItems_success() {
        server.expect(requestTo("http://localhost/testGame/shop"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [
                          {
                            "id": "shield",
                            "name": "Shield",
                            "cost": 50
                          }
                        ]
                        """, MediaType.APPLICATION_JSON));

        List<ShopItem> items = apiClient.getShopItems("testGame");

        assertThat(items).hasSize(1);
        assertThat(items.getFirst().id()).isEqualTo("shield");
        assertThat(items.getFirst().cost()).isEqualTo(50);
    }

    @Test
    void buyItem_success() {
        server.expect(requestTo("http://localhost/testGame/shop/buy/shield"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                          "shoppingSuccess": true,
                          "gold": 50,
                          "lives": 3,
                          "level": 1,
                          "turn": 6
                        }
                        """, MediaType.APPLICATION_JSON));

        BuyItemResponse response = apiClient.buyItem("testGame", "shield");

        assertThat(response.shoppingSuccess()).isTrue();
        assertThat(response.gold()).isEqualTo(50);
        assertThat(response.turn()).isEqualTo(6);
    }
}
