public class Card {
    
    private String card_holder;
    private int last_four;
    private String expiry;

    
    Card(String ch, int lf, String e)
    {
        this.card_holder = ch; 
        this.last_four = lf;
        this.expiry  = e;
    }


    public String getCard_holder() {
        return card_holder;
    }


    public void setCard_holder(String card_holder) {
        this.card_holder = card_holder;
    }


    public int getLast_four() {
        return last_four;
    }


    public void setLast_four(int last_four) {
        this.last_four = last_four;
    }


    public String getExpiry() {
        return expiry;
    }


    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    
}
