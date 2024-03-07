package com.elliott.tworoomsandaboom.card;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class ActiveCardNames implements ActiveCards {
    private static final String conditionColumn = "cardTitle";

    private String[] activeCardNames;

    @Override
    public String getDatabaseInput() {
        return Arrays.stream(activeCardNames)
                .map(name -> "\"" + name + "\"")
                .collect(Collectors.joining(", "));
    }

    @Override
    public String getConditionColumn()
    {
        return conditionColumn;
    }

    public String[] getActiveCardNames()
    {
        return activeCardNames;
    }
}
