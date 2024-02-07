package com.elliott.tworoomsandaboom.card;

import java.util.ArrayList;
import java.util.List;

import com.elliott.tworoomsandaboom.error.GameRuleException;
import com.elliott.tworoomsandaboom.util.CardConstants;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicCardsTest
{
    private static BasicCards basicCards;

    @BeforeAll
    static void setUp()
    {
        List<Card> cards = new ArrayList<>();
        cards.add(CardConstants.PRESIDENT_CARD);
        cards.add(CardConstants.BOMBER_CARD);
        cards.add(CardConstants.BLUE_TEAM_CARD);
        cards.add(CardConstants.RED_TEAM_CARD);
        cards.add(CardConstants.GAMBLER_CARD);
        basicCards = new BasicCards(cards);
    }

    @Test
    void shouldAssignBasicCardsCorrectly()
    {
        assertEquals(CardConstants.PRESIDENT_CARD, basicCards.getPresident());
        assertEquals(CardConstants.BOMBER_CARD, basicCards.getBomber());
        assertEquals(CardConstants.BLUE_TEAM_CARD, basicCards.getBlueTeam());
        assertEquals(CardConstants.RED_TEAM_CARD, basicCards.getRedTeam());
        assertEquals(CardConstants.GAMBLER_CARD, basicCards.getGambler());
    }
    
    @Test
    void shouldThrowGameRuleExceptionWhenInvalidNumberOfCardsGiven()
    {
        List<Card> errorCards = new ArrayList<>();
        Assertions.assertThrows(GameRuleException.class, () -> new BasicCards(errorCards));
        errorCards.add(new Card());
        Assertions.assertThrows(GameRuleException.class, () -> new BasicCards(errorCards));
        errorCards.add(new Card());
        Assertions.assertThrows(GameRuleException.class, () -> new BasicCards(errorCards));
        errorCards.add(new Card());
        Assertions.assertThrows(GameRuleException.class, () -> new BasicCards(errorCards));
        errorCards.add(new Card());
        Assertions.assertThrows(GameRuleException.class, () -> new BasicCards(errorCards));
        errorCards.add(new Card());
        errorCards.add(new Card());
        Assertions.assertThrows(GameRuleException.class, () -> new BasicCards(errorCards));
    }
}
