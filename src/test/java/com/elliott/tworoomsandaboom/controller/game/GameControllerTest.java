package com.elliott.tworoomsandaboom.controller.game;

import com.elliott.tworoomsandaboom.card.ActiveCardIds;
import com.elliott.tworoomsandaboom.card.ActiveCardNames;
import com.elliott.tworoomsandaboom.card.BasicCards;
import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.dao.game.GameDAO;
import com.elliott.tworoomsandaboom.dao.player.PlayerDAO;
import com.elliott.tworoomsandaboom.game.GameOperations;
import com.elliott.tworoomsandaboom.game.Room;
import com.elliott.tworoomsandaboom.player.Player;
import com.elliott.tworoomsandaboom.util.CardConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameControllerTest {
    private GameController gameController;

    @Mock
    private GameDAO gameDaoMock;
    @Mock
    private PlayerDAO playerDaoMock;

    @BeforeEach
    void setUp()
    {
        gameController = new GameController(gameDaoMock, playerDaoMock, new GameOperations());
    }

    @Test
    void shouldReturnCreatedStatusOnSetCardsWithIdsEndpoint()
    {
        int[] activeCardIds = new int[] {1, 2, 3, 4};
        ActiveCardIds activeCards = new ActiveCardIds(activeCardIds);
        ResponseEntity<String> response = gameController.setActiveCards(activeCards);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void shouldReturnCreatedStatusOnSetCardsWithNamesEndpoint()
    {
        String[] activeCardNames = new String[] {"Angel", "Blind", "Agoraphobe"};
        ActiveCardNames activeCards = new ActiveCardNames(activeCardNames);
        ResponseEntity<String> response = gameController.setActiveCards(activeCards);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void shouldReturnOkStatusOnStartGameEndpoint()
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

        when(playerDaoMock.getPlayers()).thenReturn(players);
        when(gameDaoMock.getBasicCards()).thenReturn(basicCards);
        when(gameDaoMock.getActiveCards()).thenReturn(new ArrayList<>());

        ResponseEntity<String> response = gameController.startGame();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnOkStatusOnRevealCardsEndpoint()
    {
        ResponseEntity<String> response = gameController.revealCards(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnOkStatusOnEndGameEndpoint()
    {
        ResponseEntity<String> response = gameController.endGame(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnGameStatesOnGetGameStateEndpoint()
    {
        when(gameDaoMock.getGameState(1)).thenReturn(GameState.WAITING_FOR_HOST);
        ResponseEntity<String> response = gameController.getGameState(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(GameState.WAITING_FOR_HOST, GameState.valueOf(response.getBody()));
    }

    @Test
    void shouldReturnOkStatusAndRoomOnGetRoomEndpoint()
    {
        when(gameDaoMock.getRoom(123)).thenReturn(Room.A);

        ResponseEntity<Room> response = gameController.getRoom(123);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Room.A, response.getBody());
    }
}
