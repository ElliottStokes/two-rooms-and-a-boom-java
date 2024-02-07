package com.elliott.tworoomsandaboom.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Player
{
    private int playerId;
    private String username;

    @Override
    public String toString()
    {
        return String.format("%s (%d)", this.username, this.playerId);
    }
}
