package pt.isel;

public class PersonDto2PersonBaseline implements Mapper<PersonDto, Person> {
    @Override
    public Person mapFrom(PersonDto src) {
        return new Person(src.getName(), src.getFrom());
    }
}
