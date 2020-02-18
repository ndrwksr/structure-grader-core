package edu.dselent.assignment2.card.student.perfect;

/**
 * Class to represent a card <br>
 * Implements the {@link Comparable} interface to compare cards
 * 
 * <p>
 * 
 * A Card is made up of a {@link Rank} and {@link Suit}
 *
 * @author Doug
 *
 */
@ProfessorAnnotation
public class Card
{
	/**
	 * The rank of the card
	 */
	private Rank rank;

	/**
	 * The suit of the card
	 */
	private Suit suit;

	/**
	 * Constructs a card with the given rank and suit <br>
	 * Assigns parameters to their corresponding instance variables
	 *
	 * @param rank The rank of the card
	 * @param suit The suit of the card
	 */
	public Card(Rank rank, Suit suit)
	{
		this.rank = rank;
		this.suit = suit;
	}

	/**
	 * Copy constructor <br>
	 * Constructs a card with the rank and suit from the other card <br>
	 * Assigns the rank and suit of the other card to the rank and suit of this card
	 *
	 * @param otherCard The other card to be copied from
	 */
	public Card(Card otherCard)
	{
		this.rank = otherCard.getRank();
		this.suit = otherCard.getSuit();
	}

	/**
	 * Getter method <br>
	 * @return Returns the rank of the card
	 */
	public Rank getRank()
	{
		return rank;
	}

	/**
	 * Getter method
	 * @return Returns the suit of the card
	 */
	public Suit getSuit()
	{
		return suit;
	}
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((suit == null) ? 0 : suit.hashCode());
		return result;
	}

	/**
	 * Determines if the two Card objects are equal <br>
	 * Equality is determined based on the rank and suit of the card
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
		
		if (!(obj instanceof Card))
		{
			return false;
		}
		
		Card other = (Card) obj;
		
		if (rank != other.rank)
		{
			return false;
		}
		
		if (suit != other.suit)
		{
			return false;
		}
		
		return true;
	}

	/**
	 *  Returns the string representation of the card <br>
	 *  The String representation must match mine and should leverage {@link Rank#toString()} and {@link Suit#toString()}
	 *  
	 *  <p>
	 *  
	 *  Examples
	 *  
	 *  <p>
	 *  
	 *  "ace of hearts" <br>
	 *  "two of spades"
	 *  
	 *  @see Object#toString()
	 *  @return Returns the string representation of the card
	 */
	@Override
	public String toString()
	{
		return rank.toString() + " of " + suit.toString();
	}

}
