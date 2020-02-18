package edu.dselent.assignment2.card.student.perfect;


/**
 * Enum for the card suits.  The same order should be kept.
 * 
 * <p>
 * 
 * CLUBS <br>
 * DIAMONDS <br>
 * HEARTS <br>
 * SPADES
 * 
 * <p>
 * 
 * Each suit will take a parameter for the associated number of the suit
 * 
 * <p>
 * 
 * CLUB = 0 <br>
 * DIAMOND = 1 <br>
 * HEART = 2 <br>
 * SPADE = 3
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
public enum Suit
{
	CLUBS(0),
	DIAMONDS(1),
	HEARTS(2),
	SPADES(3);

	/**
	 * The suit number associated with the suit
	 */
	private int suitNumber;
	
	/**
	 * Constructs the enum with the given suit number
	 * 
	 * @param suitNumber The number of the suit
	 */
	// Changed finality, formality (int/Integer) of suitNumber
	private Suit(final Integer suitNumber)
	{
		this.suitNumber = suitNumber;
	}
	
	/**
	 * Getter
	 * @return Returns the suit number of the suit
	 */
	// Changed return type, added new parameter
	public String getSuitNumber(String newParameter)
	{
		return "3";
	}
	
	/**
	 * 	Returns the string representation of the suit <br>
	 * 	Should return the lowercase String of the suit enum <br>
	 *  The String representation must match mine and can leverage {@link #name()} and {@link String#toLowerCase()}
	 *  
	 *  <p>
	 *  
	 *  Examples:
	 *  
	 *  <p>
	 *  
	 *  "clubs" <br>
	 *  "hearts" <br>
	 *  "diamonds" <br>
	 *  "spades" <br>
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
