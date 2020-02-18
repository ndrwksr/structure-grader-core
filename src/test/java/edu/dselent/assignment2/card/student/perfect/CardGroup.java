package edu.dselent.assignment2.card.student.perfect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A class to represent a group of cards such as a card deck or a hand.
 * 
 * @author Doug
 *
 */
public class CardGroup
{
	/**
	 * A list of cards, such as a deck or hand
	 */
	private List<Card> cardList;

	/**
	 * Constructs a new CardGroup and initializes cardList to be an empty ArrayList
	 */
	public CardGroup()
	{
		cardList = new ArrayList<Card>();
	}

	/**
	 * Copy constructor <br>
	 * Constructs a group of cards from another CardGroup <br>
	 *
	 * <p>
	 *
	 * Initializes cardList to an empty ArrayList <br>
	 * Performs a deep copy of the cardList in the other card group into the card list in this card group
	 *
	 * @param otherGroup The other card group to copy into this one
	 */
	public CardGroup(CardGroup otherGroup)
	{
		cardList = new ArrayList<Card>();
		List<Card> otherCardList = otherGroup.getCardList();

		for(Card card : otherCardList)
		{
			cardList.add(new Card(card));
		}
	}

	/**
	 * Getter method
	 * @return Returns the cardLList
	 */
	public List<Card> getCardList()
	{
		return cardList;
	}

	/**
	 * Adds the given card to the cardList
	 *
	 * @see List#add(Object)
	 * @param card The card to add to the list
	 * @return Returns the result of cardList.add
	 */
	public boolean addCard(Card card)
	{
		return cardList.add(card);
	}

	/**
	 * Removes the last card in the cardList
	 *
	 * @see List#remove(int)
	 * @return Returns the result of cardList.remove
	 */
	public Card removeLastCard()
	{
		return cardList.remove(cardList.size()-1);
	}

	/**
	 * Removed the card from the cardList
	 *
	 * @see List#remove(Object)
	 * @param card The card to remove
	 * @return Returns the result of cardList.remove
	 */
	public boolean removeCard(Card card)
	{
		return cardList.remove(card);
	}
	
	/**
	 * Returns the last card in cardList
	 * 
	 * @see List#get(int)
	 * @return Returns the last card in cardList
	 */
	public Card getLastCard()
	{
		return cardList.get(cardList.size()-1);
	}
	
	/**
	 * Determines is the card group is empty
	 * 
	 * @see List#isEmpty()
	 * @return Returns true is the group is empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		return cardList.isEmpty();
	}
	
	/**
	 * Returns the size of the card group
	 * 
	 * @see List#size()
	 * @return Returns the size of cardList
	 */
	public int size()
	{
		return cardList.size();
	}

	/**
	 * Shuffles the deck of cards
	 * 
	 * @see Collections#shuffle(List, Random)
	 * @param random The Random object to use when shuffling
	 */
	public void shuffle(Random random)
	{
		Collections.shuffle(cardList, random);
	}

	
	/**
	 * Clears the cardList
	 * 
	 * @see List#clear()
	 */
	public void clear()
	{
		cardList.clear();
	}

	/**
	 * @see Object#hashCode()
	 * @return The hashcode for the object
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardList == null) ? 0 : cardList.hashCode());
		return result;
	}

	
	/**
	 * Determines if the two CardGroup objects are equal <br>
	 * Equality is determined based on the cardList
	 * 
	 * @see Object#equals(Object)
	 * @return True is equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null)
		{
			return false;
		}
		
		if (!(obj instanceof CardGroup))
		{
			return false;
		}
		
		CardGroup other = (CardGroup) obj;
		
		if (cardList == null)
		{
			if (other.cardList != null)
			{
				return false;
			}
			
		}
		else if (!cardList.equals(other.cardList))
		{
			return false;
		}
		
		return true;
	}

	/**
	 *  Returns the string representation of the card group <br>
	 *  Should automatically generate this method based on cardList.  The String representation must match mine.
	 *  
	 *  @see Object#toString()
	 *  @return Returns the string representation of the card group
	 */
	@Override
	public String toString()
	{
		return "CardGroup [cardList=" + cardList + "]";
	}

}
