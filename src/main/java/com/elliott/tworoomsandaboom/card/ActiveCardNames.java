package com.elliott.tworoomsandaboom.card;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class ActiveCardNames implements ActiveCards {
    private String[] activeCardNames;

    @Override
    public String getDatabaseInput() {
        return Arrays.stream(activeCardNames)
                .map(name -> "\"" + name + "\"")
                .collect(Collectors.joining(", "));
    }

    public String[] getActiveCardNames()
    {
        return activeCardNames;
    }
}
