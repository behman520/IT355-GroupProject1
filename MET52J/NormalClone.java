package MET52J;

public class NormalClone implements Cloneable {
    
    private Person person;

    NormalClone(Person p)
    {
        this.person = p;
    }

    @Override 
    public NormalClone clone()
    {
        Person newPerson;

        String name = this.person.getName();
        int age = this.person.getAge();

        newPerson = new Person(name, age);

        return new NormalClone(newPerson);

    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    
}
