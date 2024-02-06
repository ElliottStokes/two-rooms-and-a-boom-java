package com.elliott.tworoomsandaboom.game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.elliott.tworoomsandaboom.card.BasicCards;
import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.error.GameRuleException;
import com.elliott.tworoomsandaboom.player.Player;

import org.springframework.stereotype.Component;

@Component
public class GameOperations
{
    public Map<Player, Card> dealCards(Player[] players, BasicCards basicCards, List<Card> activeCards)
    {
        int numberOfPlayers = players.length;

        if (numberOfPlayers < 6)
            throw new GameRuleException("Not enough players (need more than 6)");
        if (numberOfPlayers > 30)
            throw new GameRuleException("Too many players (no more than 30)");

        List<Card> unassignedCards = new ArrayList<>();
        unassignedCards.add(basicCards.getPresident());
        unassignedCards.add(basicCards.getBomber());
        if (numberOfPlayers % 2 > 0 && activeCards.stream().anyMatch(card -> card.getCardId() == 3))
            unassignedCards.add(basicCards.getGambler());

        unassignedCards.addAll(activeCards);
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
        return assignedCards;
    }
}
