package com.elliott.tworoomsandaboom.card;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActiveCardsTest
{
    @Test
    void shouldConvertIdsToDatabaseQueryFormat()
    {
        int[] activeCardIds = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
        ActiveCards activeCards = new ActiveCardIds(activeCardIds);

        assertEquals("1, 2, 3, 4, 5, 6, 7, 8, 9", activeCards.getDatabaseInput());
    }

    @Test
    void shouldConvertNamesToDatabaseQueryFormat()
    {
        String[] activeCardNames = new String[] {"Angel", "Blind", "Agoraphobe"};
        ActiveCards activeCards = new ActiveCardNames(activeCardNames);

        assertEquals("\"Angel\", \"Blind\", \"Agoraphobe\"", activeCards.getDatabaseInput());
    }
}
