
/**
 * Represents a credit card with basic information.
 * <p>
 * Only the last four digits of the card number are stored, which avoids
 * keeping the full card number in memory or in persisted data. In a real
 * scenario
 * a user's card company would be contacted for the full card information
 * </p>
 */
public class Card {

    /** The name on the card */
    private String card_holder;
    /** The last four digits of the card number. */
    private String last_four;

    /** The card's expiration date, for example, "MM/YY" */
    private String expiry;

    /**
     * Constructs a new {@code Card} with the specified details.
     * <p>
     * This constructor can only be called from classes within the same directory
     * </p>
     *
     * @param ch the name of the card holder
     * @param lf the last four digits of the card number
     * @param e  the expiration date of the card
     */
    Card(String ch, String lf, String e) {
        this.card_holder = ch;
        this.last_four = lf;
        this.expiry = e;
    }

    /**
     * Returns the name of the card holder.
     *
     * @return the card holder's name
     */
    public String getCard_holder() {
        return card_holder;
    }

    /**
     * Sets the name of the card holder.
     *
     * @param card_holder the new card holder's name
     */
    public void setCard_holder(String card_holder) {
        this.card_holder = card_holder;
    }

    /**
     * Returns the last four digits of the card number.
     *
     * @return the last four digits of the card number
     */
    public String getLast_four() {
        return last_four;
    }

    /**
     * Sets the last four digits of the card number.
     *
     * @param last_four the last four digits of the card number
     */
    public void setLast_four(String last_four) {
        this.last_four = last_four;
    }

    /**
     * Returns the expiration date of the card.
     *
     * @return the card's expiration date
     */
    public String getExpiry() {
        return expiry;
    }

    /**
     * Sets the expiration date of the card.
     *
     * @param expiry the new expiration date of the card
     */
    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

}
