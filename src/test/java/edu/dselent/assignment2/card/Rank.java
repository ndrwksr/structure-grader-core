package edu.dselent.assignment2.card;

/**
 * Enum for the card ranks. The same order should be kept.
 * 
 * <p>
 * 
 * ACE <br>
 * TWO <br>
 * THREE <br>
 * FOUR <br>
 * FIVE <br>
 * SIX <br>
 * SEVEN <br>
 * EIGHT <br>
 * NINE <br>
 * TEN <br>
 * JACK <br>
 * QUEEN <br>
 * KING
 * 
 * <p>
 * 
 * Each rank will take a parameter for the associated number of the rank
 * 
 * <p>
 * 
 * ACE = 0 <br>
 * TWO = 1 <br>
 * THREE = 2 <br>
 * FOUR = 3 <br>
 * FIVE = 4 <br>
 * SIX = 5 <br>
 * SEVEN = 6  <br>
 * EIGHT = 7 <br>
 * NINE = 8 <br>
 * TEN = 9 <br>
 * JACK = 10 <br>
 * QUEEN = 11 <br>
 * KING = 12 <br>
 * 
 * <p>
 * 
 * Note that the Javadocs for this class contain the methods valueOf and values().  These
 * methods are automatically added by the compiler and you do not have to implement them.<br>
 * This enumeration should be similar to the Fruit example we did in class.
 * 
 * @author Doug
 *
 */
public enum Rank
{
	ACE(0),
	TWO(1),
	THREE(2),
	FOUR(3),
	FIVE(4),
	SIX(5),
	SEVEN(6),
	EIGHT(7),
	NINE(8),
	TEN(9),
	JACK(10),
	QUEEN(11),
	KING(12);
	
	/**
	 * The rank number associated with the rank
	 */
	private int rankNumber;
	
	/**
	 * Constructs the enum with the given rank number
	 * 
	 * @param rankNumber The number of the rank
	 */
	private Rank(int rankNumber)
	{
		this.rankNumber = rankNumber;
	}
	
	/**
	 * Getter
	 * @return Returns the rank number of the rank
	 */
	public int getCardNumber()
	{
		return this.rankNumber;
	}
	
	/**
	 * 	Returns the string representation of the rank <br>
	 * 	Should return the lowercase String of the rank enum <br>
	 *  The String representation must match mine and can leverage {@link #name()} and {@link String#toLowerCase()}
	 *  
	 *  <p>
	 *  
	 *  Examples:
	 *  
	 *  <p>
	 *  
	 *  "ace" <br>
	 *  "two" <br>
	 *  "three" <br>
	 *  "..." <br>
	 *  "Jack" <br>
	 *  "Queen" <br>
	 *  "King" <br>
	 *  
	 * 	@see Object#toString()
	 *  @return Returns the string representation of the card
	 */
	@Override
	public String toString()
	{
		return name().toLowerCase();
	}

}
