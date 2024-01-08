package com.elliott.tworoomsandaboom.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.elliott.tworoomsandaboom.card.ActiveCards;
import com.elliott.tworoomsandaboom.card.BasicCards;
import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.dao.TwoRoomsAndABoomDAO;
import com.elliott.tworoomsandaboom.error.GameRuleException;
import com.elliott.tworoomsandaboom.player.Player;
import com.elliott.tworoomsandaboom.player.RegisterPlayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/two-rooms-and-a-boom")
public class TwoRoomsAndABoomController
{
    private final TwoRoomsAndABoomDAO twoRoomsAndABoomDAO;

    @Autowired
    public TwoRoomsAndABoomController(TwoRoomsAndABoomDAO twoRoomsAndABoomDAO)
    {
        this.twoRoomsAndABoomDAO = twoRoomsAndABoomDAO;
    }
    @PostMapping(value = "/join",
                 consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Player> registerNewPlayer(
            @RequestBody
            RegisterPlayer newPlayer
    )
    {
        log.info("Register New Player: {}", newPlayer.getUsername());
        Player player = twoRoomsAndABoomDAO.createNewPlayer(newPlayer);
        log.info("Created New Player [playerId: {}, username: {}]", player.getPlayerId(), player.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(player);
    }

    @PutMapping("/ready")
    public ResponseEntity<String> readyUp(
            @RequestParam("playerId")
            int playerId
    )
    {
        log.info("Ready Up: {}", playerId);
        twoRoomsAndABoomDAO.readyUpPlayer(playerId);
        return ResponseEntity.ok().body("");
    }

    @GetMapping("/card")
    public ResponseEntity<Card> getCard(
            @RequestParam("playerId")
            int playerId
    )
    {
        log.info("Get Card: {}", playerId);
        Card card = twoRoomsAndABoomDAO.getCard(playerId);
        return ResponseEntity.ok().body(card);
    }

    @GetMapping(value = "/cardImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getCardImage(
            @RequestParam("playerId")
            int playerId
    ) throws IOException
    {
        String filePath = twoRoomsAndABoomDAO.getCardFilePath(playerId);
        final ByteArrayResource image = new ByteArrayResource(Files.readAllBytes(Paths.get(
                "src/main/resources/cards/" + filePath
        )));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(image.contentLength())
                .body(image);
    }

    @PutMapping(value = "/setCards",
                consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> setActiveCards(
            @RequestBody
            ActiveCards activeCards
    )
    {
        log.info("Set Active Cards: {}", activeCards.getActiveCardIds());
        twoRoomsAndABoomDAO.setActiveCards(activeCards);
        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }

    @GetMapping("/assignCards")
    public ResponseEntity<Map<Player, Card>> assignCards()
    {
        Player[] players = twoRoomsAndABoomDAO.getPlayers();
        int numberOfPlayers = players.length;

        if (numberOfPlayers < 6)
            throw new GameRuleException("Not enough players (need more than 6)");
        if (numberOfPlayers > 30)
            throw new GameRuleException("Too many players (no more than 30)");

        BasicCards basicCards = twoRoomsAndABoomDAO.getBasicCards();
        //List<Card> activeCards = getActiveCards();

        List<Card> unassignedCards = new ArrayList<>();
        unassignedCards.add(basicCards.getPresident());
        unassignedCards.add(basicCards.getBomber());
        if (numberOfPlayers % 2 > 0)
            unassignedCards.add(basicCards.getGambler());

        while (unassignedCards.size() < numberOfPlayers)
        {
            unassignedCards.add(basicCards.getBlueTeam());
            unassignedCards.add(basicCards.getRedTeam());
        }

        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        Map<Player, Card> assignedCards = new LinkedHashMap<>();
        for (Player player : players)
        {
            int cardIndex = threadLocalRandom.nextInt(0, unassignedCards.size());
            assignedCards.put(player, unassignedCards.remove(cardIndex));
        }
        log.info("Assigned cards: {}", assignedCards);
        twoRoomsAndABoomDAO.saveAssignedCards(assignedCards);
        return ResponseEntity.ok(assignedCards);
    }
}
