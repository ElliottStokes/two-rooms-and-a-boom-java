package com.elliott.tworoomsandaboom.controller.card;

import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.dao.card.CardDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardControllerTest {
    private CardController cardController;

    @Mock
    private CardDAO cardDaoMock;

    @BeforeEach
    void setUp()
    {
        cardController = new CardController(cardDaoMock);
    }

    @Test
    void shouldReturnOkStatusAndCardOnCardEndpoint()
    {
        int playerId = 1;

        when(cardDaoMock.getCard(playerId)).thenReturn(new Card(1, 1, "President"));

        ResponseEntity<Card> response = cardController.getCard(playerId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("President", response.getBody().getTitle());
    }

    @Test
    void shouldReturnOkStatusOnCardImageEndpoint() throws IOException
    {
        int playerId = 1;

        when(cardDaoMock.getCardFilePath(playerId)).thenReturn("blue/President.png");

        ResponseEntity<Resource> response = cardController.getCardImage(playerId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
