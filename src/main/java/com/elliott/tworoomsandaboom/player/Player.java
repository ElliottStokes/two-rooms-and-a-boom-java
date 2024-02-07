package com.elliott.tworoomsandaboom.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
