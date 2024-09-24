package com.elliott.tworoomsandaboom.game;

public enum Room {
    A('A'), B('B');

    Room(char room) {
        this.room = room;
    }

    public final char room;
}
