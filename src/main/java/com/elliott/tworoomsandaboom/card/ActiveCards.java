package com.elliott.tworoomsandaboom.card;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveCards
{
    private int[] activeCardIds;

    public String getDatabaseInput()
    {
        String activeCardIdsString = Arrays.toString(activeCardIds);
        return activeCardIdsString.substring(1, activeCardIdsString.length()-1);
    }
}
