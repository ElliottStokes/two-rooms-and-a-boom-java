package com.elliott.tworoomsandaboom.controller.player;

import com.elliott.tworoomsandaboom.dao.player.PlayerDAO;
import com.elliott.tworoomsandaboom.player.Player;
import com.elliott.tworoomsandaboom.player.RegisterPlayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/two-rooms-and-a-boom/player")
public class PlayerController {
    private final PlayerDAO playerDAO;

    @Autowired
    public PlayerController(PlayerDAO playerDAO) {
        this.playerDAO = playerDAO;
    }

    @PostMapping(value = "/join",
            consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Player> registerNewPlayer(
            @RequestBody
            RegisterPlayer player
    )
    {
        log.info("Register Player: {}", player.getUsername());
        Player playerDetails;
        Optional<Player> existingPlayer = playerDAO.getPlayerDetailsFromUsername(player.getUsername());
        if (existingPlayer.isPresent()) {
            playerDetails = existingPlayer.get();
            log.info("Player already exists [playerId: {}, username: {}]", playerDetails.getPlayerId(), playerDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(playerDetails);
        } else {
            playerDetails = playerDAO.createNewPlayer(player);
            log.info("Created New Player [playerId: {}, username: {}]", playerDetails.getPlayerId(), playerDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(playerDetails);
        }
    }

    @PutMapping("/ready")
    public ResponseEntity<String> readyUp(
            @RequestParam("playerId")
            int playerId
    )
    {
        // NOT IMPLEMENTED YET!
        log.info("Ready Up: {}", playerId);
        playerDAO.readyUpPlayer(playerId);
        return ResponseEntity.ok().body("");
    }

    @GetMapping("/checkCredentials")
    public ResponseEntity<String> checkCredentials(
            @RequestParam("playerId")
            int playerId,
            @RequestParam("username")
            String username
    ) {
        boolean isValidPlayer = playerDAO.checkCredentials(playerId, username);
        log.info("Checking credentials for player [playerId: {}, username: {}]: {}", playerId, username, isValidPlayer);

        if (isValidPlayer) {
            return ResponseEntity.ok().body("");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    @GetMapping("/listAll")
    public ResponseEntity<Player[]> listAllPlayers()
    {
        Player[] players = playerDAO.getPlayers();
        log.info("Listing all players: {}", players);
        return ResponseEntity.ok(players);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePlayer(
            @RequestParam("playerId")
            int playerId
    )
    {
        log.info("Deleting player: {}", playerId);
        playerDAO.deletePlayer(playerId);
        return ResponseEntity.ok().body("");
    }
}
