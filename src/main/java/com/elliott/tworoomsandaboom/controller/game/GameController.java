package com.elliott.tworoomsandaboom.controller.game;

import com.elliott.tworoomsandaboom.card.ActiveCardIds;
import com.elliott.tworoomsandaboom.card.ActiveCardNames;
import com.elliott.tworoomsandaboom.card.BasicCards;
import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.dao.game.GameDAO;
import com.elliott.tworoomsandaboom.game.GameOperations;
import com.elliott.tworoomsandaboom.dao.player.PlayerDAO;
import com.elliott.tworoomsandaboom.game.Room;
import com.elliott.tworoomsandaboom.player.AssignedPlayer;
import com.elliott.tworoomsandaboom.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/two-rooms-and-a-boom/game")
public class GameController {
    private final GameDAO gameDAO;
    private final PlayerDAO playerDAO;
    private final GameOperations gameOperations;

    @Autowired
    public GameController(GameDAO gameDAO, PlayerDAO playerDAO, GameOperations gameOperations) {
        this.gameDAO = gameDAO;
        this.playerDAO = playerDAO;
        this.gameOperations = gameOperations;
    }

    @PutMapping(value = "/setCards/id",
            consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> setActiveCards(
            @RequestBody
            ActiveCardIds activeCardIds
    )
    {
        log.info("Set Active Cards: {}", activeCardIds.getActiveCardIds());
        gameDAO.setActiveCards(activeCardIds);
        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }

    @PutMapping(value = "/setCards/name",
            consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> setActiveCards(
            @RequestBody
            ActiveCardNames activeCardNames
    )
    {
        log.info("Set Active Cards: {}", Arrays.toString(activeCardNames.getActiveCardNames()));
        gameDAO.setActiveCards(activeCardNames);
        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }

    @GetMapping(value = "/clearActiveCards")
    public ResponseEntity<String> clearActiveCards()
    {
        log.info("Clearing Active Cards");
        gameDAO.removeActiveCards();
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @GetMapping("/state")
    public ResponseEntity<String> getGameState(
            @RequestParam("gameId")
            int gameId
    )
    {
        GameState gameState = gameDAO.getGameState(gameId);
        return ResponseEntity.ok().body(gameState.toString());
    }

    @GetMapping("/revealTime")
    public ResponseEntity<String> getRevealTime(
            @RequestParam("gameId")
            int gameId
    )
    {
        String revealTime = gameDAO.getRevealTime(gameId);
        return ResponseEntity.ok().body(revealTime);
    }

    @GetMapping("/room")
    public ResponseEntity<Room> getRoom(
            @RequestParam("playerId")
            int playerId
    )
    {
        log.info("Get Room: {}", playerId);
        Room room = gameDAO.getRoom(playerId);
        return ResponseEntity.ok().body(room);
    }

    @GetMapping("/startGame")
    public ResponseEntity<String> startGame()
    {
        // When multiple games can be played at once, this will need to be updated
        // possibly by passing in a game ID as a URL parameter
        int gameId = 1;
        Player[] players = playerDAO.getPlayers();
        BasicCards basicCards = gameDAO.getBasicCards();
        List<Card> activeCards = gameDAO.getActiveCards();
        Map<Player, Card> assignedCards = gameOperations.dealCards(players, basicCards, activeCards);
        Map<Player, Room> assignedRooms = gameOperations.assignRooms(players);
        List<AssignedPlayer> assignedPlayers = new ArrayList<>();
        Arrays.stream(players).forEach(
            player -> assignedPlayers.add(
                new AssignedPlayer(
                    gameId,
                    player,
                    assignedCards.get(player),
                    assignedRooms.get(player)
                )
            )
        );
        gameDAO.saveAssignedPlayers(assignedPlayers);
        gameDAO.setGameState(GameState.IN_PROGRESS, gameId);
        return ResponseEntity.ok().body("");
    }

    @GetMapping("/revealCards")
    public ResponseEntity<String> revealCards(
        @RequestParam("gameId")
        int gameId
    )
    {
        gameDAO.setGameState(GameState.REVEAL_CARDS, gameId);
        gameDAO.setRevealTime(gameId);
        return ResponseEntity.ok().body("");
    }

    @GetMapping("/endGame")
    public ResponseEntity<String> endGame(
        @RequestParam("gameId")
        int gameId
    )
    {
        log.info("Ending Game");
        gameDAO.clearAssignedPlayers(gameId);
        gameDAO.setGameState(GameState.WAITING_FOR_HOST, gameId);
        return ResponseEntity.ok().body("");
    }
}
