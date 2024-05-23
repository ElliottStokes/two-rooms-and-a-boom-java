package com.elliott.tworoomsandaboom.card;

import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ActiveCardIds implements ActiveCards
{
    private int[] activeCardIds;

    public String getDatabaseInput()
    {
        return Arrays.stream(activeCardIds)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(", "));
    }

    public int[] getActiveCardIds()
    {
        return activeCardIds;
    }
}
