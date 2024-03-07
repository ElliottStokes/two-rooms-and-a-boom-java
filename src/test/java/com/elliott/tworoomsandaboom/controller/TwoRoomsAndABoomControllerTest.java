package com.elliott.tworoomsandaboom.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elliott.tworoomsandaboom.card.ActiveCardIds;
import com.elliott.tworoomsandaboom.card.ActiveCardNames;
import com.elliott.tworoomsandaboom.card.BasicCards;
import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.dao.TwoRoomsAndABoomDAO;
import com.elliott.tworoomsandaboom.game.GameOperations;
import com.elliott.tworoomsandaboom.player.Player;
import com.elliott.tworoomsandaboom.player.RegisterPlayer;
import com.elliott.tworoomsandaboom.util.CardConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TwoRoomsAndABoomControllerTest
{
    private TwoRoomsAndABoomController twoRoomsAndABoomController;
    private GameOperations gameOperations;

    @Mock
    private TwoRoomsAndABoomDAO twoRoomsAndABoomDaoMock;

    @BeforeEach
    void setUp()
    {
        gameOperations = new GameOperations();
        twoRoomsAndABoomController = new TwoRoomsAndABoomController(
                twoRoomsAndABoomDaoMock,
                gameOperations
        );
    }

    @Test
    void shouldReturnCreatedStatusAndPlayerObjectOnJoinEndpoint()
    {
        RegisterPlayer player = new RegisterPlayer("testPlayer");

        when(twoRoomsAndABoomDaoMock.createNewPlayer(player)).thenReturn(new Player(1, "testPlayer"));

        ResponseEntity<Player> response = twoRoomsAndABoomController.registerNewPlayer(player);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(player.getUsername(), response.getBody().getUsername());
    }

    @Test
    void shouldReturnOkStatusAndCardOnCardEndpoint()
    {
        int playerId = 1;

        when(twoRoomsAndABoomDaoMock.getCard(playerId)).thenReturn(new Card(1, 1, "President"));

        ResponseEntity<Card> response = twoRoomsAndABoomController.getCard(playerId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("President", response.getBody().getTitle());
    }

    @Test
    void shouldReturnOkStatusOnCardImageEndpoint() throws IOException
    {
        int playerId = 1;

        when(twoRoomsAndABoomDaoMock.getCardFilePath(playerId)).thenReturn("blue/President.png");

        ResponseEntity<Resource> response = twoRoomsAndABoomController.getCardImage(playerId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnCreatedStatusOnSetCardsWithIdsEndpoint()
    {
        int[] activeCardIds = new int[] {1, 2, 3, 4};
        ActiveCardIds activeCards = new ActiveCardIds(activeCardIds);
        ResponseEntity<String> response = twoRoomsAndABoomController.setActiveCards(activeCards);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void shouldReturnCreatedStatusOnSetCardsWithNamesEndpoint()
    {
        String[] activeCardNames = new String[] {"Angel", "Blind", "Agoraphobe"};
        ActiveCardNames activeCards = new ActiveCardNames(activeCardNames);
        ResponseEntity<String> response = twoRoomsAndABoomController.setActiveCards(activeCards);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void shouldReturnOkStatusAndAssignedCardsOnAssignCardsEndpoint()
    {
        Player[] players = new Player[]{
                new Player(1, "testPlayer1"),
                new Player(2, "testPlayer2"),
                new Player(3, "testPlayer3"),
                new Player(4, "testPlayer4"),
                new Player(5, "testPlayer5"),
                new Player(6, "testPlayer6")
        };
        List<Card> cards = new ArrayList<>();
        cards.add(CardConstants.PRESIDENT_CARD);
        cards.add(CardConstants.BOMBER_CARD);
        cards.add(CardConstants.BLUE_TEAM_CARD);
        cards.add(CardConstants.RED_TEAM_CARD);
        cards.add(CardConstants.GAMBLER_CARD);
        BasicCards basicCards = new BasicCards(cards);

        when(twoRoomsAndABoomDaoMock.getPlayers()).thenReturn(players);
        when(twoRoomsAndABoomDaoMock.getBasicCards()).thenReturn(basicCards);
        when(twoRoomsAndABoomDaoMock.getActiveCards()).thenReturn(new ArrayList<>());

        ResponseEntity<Map<Player, Card>> response = twoRoomsAndABoomController.assignCards();
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<Player, Card> assignedCards = response.getBody();
        assertEquals(6, assignedCards.size());
        assertTrue(assignedCards.containsValue(CardConstants.PRESIDENT_CARD));
        assertTrue(assignedCards.containsValue(CardConstants.BOMBER_CARD));
        assertTrue(assignedCards.containsValue(CardConstants.RED_TEAM_CARD));
        assertTrue(assignedCards.containsValue(CardConstants.BLUE_TEAM_CARD));
        assertFalse(assignedCards.containsValue(CardConstants.GAMBLER_CARD));
    }
}
