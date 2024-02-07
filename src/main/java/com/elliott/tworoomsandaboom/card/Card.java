package com.elliott.tworoomsandaboom.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Card
{
    private int cardId;
    private int teamId;
    private String title;

    @Override
    public String toString()
    {
        return this.title;
    }
}
