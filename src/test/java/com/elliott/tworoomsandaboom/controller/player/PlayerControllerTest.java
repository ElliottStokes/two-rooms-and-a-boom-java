package com.elliott.tworoomsandaboom.controller.player;

import com.elliott.tworoomsandaboom.dao.player.PlayerDAO;
import com.elliott.tworoomsandaboom.player.Player;
import com.elliott.tworoomsandaboom.player.RegisterPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerControllerTest
{
    private PlayerController playerController;

    @Mock
    private PlayerDAO playerDaoMock;

    @BeforeEach
    void setUp()
    {
        playerController = new PlayerController(playerDaoMock);
    }

    @Test
    void shouldReturnCreatedStatusAndPlayerObjectOnJoinEndpoint()
    {
        RegisterPlayer player = new RegisterPlayer("testPlayer");

        when(playerDaoMock.createNewPlayer(player)).thenReturn(new Player(1, "testPlayer"));

        ResponseEntity<Player> response = playerController.registerNewPlayer(player);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(player.getUsername(), response.getBody().getUsername());
    }

    @Test
    void shouldReturnExistingPlayerDetailsWhenPlayerJoinsWithExistingUsername()
    {
        int playerId = 2;
        String username = "existingPlayer";

        when(playerDaoMock.getPlayerDetailsFromUsername(username))
                .thenReturn(Optional.of(new Player(playerId, username)));

        ResponseEntity<Player> response = playerController.registerNewPlayer(new RegisterPlayer(username));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(username, response.getBody().getUsername());
        assertEquals(playerId, response.getBody().getPlayerId());
    }

    @Test
    void shouldReturnOkWhenCheckingCredentialsOfExistingPlayer()
    {
        int playerId = 1;
        String username = "testPlayer";

        when(playerDaoMock.checkCredentials(playerId, username)).thenReturn(true);

        ResponseEntity<String> response = playerController.checkCredentials(playerId, username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundWhenCheckingCredentialsOfNonExistingPlayer()
    {
        int playerId = 2;
        String username = "NotRealPlayer";

        when(playerDaoMock.checkCredentials(playerId, username)).thenReturn(false);

        ResponseEntity<String> response = playerController.checkCredentials(playerId, username);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
