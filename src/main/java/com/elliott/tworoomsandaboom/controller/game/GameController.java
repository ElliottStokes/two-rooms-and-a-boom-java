package com.elliott.tworoomsandaboom.controller.game;

import com.elliott.tworoomsandaboom.card.ActiveCardIds;
import com.elliott.tworoomsandaboom.card.ActiveCardNames;
import com.elliott.tworoomsandaboom.card.BasicCards;
import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.dao.game.GameDAO;
import com.elliott.tworoomsandaboom.game.GameOperations;
import com.elliott.tworoomsandaboom.dao.player.PlayerDAO;
import com.elliott.tworoomsandaboom.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/assignCards")
    public ResponseEntity<Map<Player, Card>> assignCards()
    {
        Player[] players = playerDAO.getPlayers();
        BasicCards basicCards = gameDAO.getBasicCards();
        List<Card> activeCards = gameDAO.getActiveCards();
        Map<Player, Card> assignedCards = gameOperations.dealCards(players, basicCards, activeCards);
        log.info("Assigned cards: {}", assignedCards);
        gameDAO.saveAssignedCards(assignedCards);
        return ResponseEntity.ok(assignedCards);
    }

    @GetMapping("/endGame")
    public ResponseEntity<String> endGame()
    {
        log.info("Ending Game");
        gameDAO.clearAssignedCards();
        return ResponseEntity.ok().body("");
    }
}
