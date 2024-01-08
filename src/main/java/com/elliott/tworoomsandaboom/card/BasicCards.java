package com.elliott.tworoomsandaboom.card;

import java.util.List;

import com.elliott.tworoomsandaboom.error.GameRuleException;

import lombok.Getter;

@Getter
public class BasicCards
{
    private Card president;
    private Card bomber;
    private Card blueTeam;
    private Card redTeam;
    private Card gambler;

    public BasicCards(List<Card> cards)
    {
        if (cards.size() == 5)
        {
            for (Card card : cards)
            {
                this.setCard(card);
            }
        }
        else
        {
            throw new GameRuleException("Incorrect number of basic cards found");
        }
    }

    private void setCard(Card card)
    {
        switch (card.getTitle())
        {
            case "President":
                this.president = card;
                break;
            case "Bomber":
                this.bomber = card;
                break;
            case "Blue Team":
                this.blueTeam = card;
                break;
            case "Red Team":
                this.redTeam = card;
                break;
            case "Gambler":
                this.gambler = card;
                break;
            default:
                throw new GameRuleException(card.getTitle() + " is not a basic card!");
        }
    }
}
