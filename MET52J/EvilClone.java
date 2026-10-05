 package MET52J;
public class EvilClone implements Cloneable   {
    
    private Person person;

    EvilClone(Person p)
    {
        this.person = p;
    }

    @Override 
    public EvilClone clone() //returns the same object instead of a new one
    {
        return this;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    
}
