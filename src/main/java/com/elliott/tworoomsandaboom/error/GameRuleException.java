package com.elliott.tworoomsandaboom.error;

public class GameRuleException extends RuntimeException
{
    public GameRuleException(String message) {
        super(message);
    }
}
