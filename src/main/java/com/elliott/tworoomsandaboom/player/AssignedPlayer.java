package com.elliott.tworoomsandaboom.player;

import com.elliott.tworoomsandaboom.card.Card;
import com.elliott.tworoomsandaboom.game.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignedPlayer {
    private int gameId;
    private Player player;
    private Card card;
    private Room room;

    public String toDatabaseInputString()
    {
        return String.format("(%d,%d,%d,'%s')",
                this.gameId,
                this.player.getPlayerId(),
                this.card.getCardId(),
                this.room.toString()
        );
    }

    @Override
    public String toString()
    {
        return String.format("(%d)%s[card: %s, room: %s]",
                this.player.getPlayerId(),
                this.player.getUsername(),
                this.card.getTitle(),
                this.room.name()
        );
    }
}
