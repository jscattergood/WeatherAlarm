package weatherAlarm.model;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import weatherAlarm.util.PredicateEnum;

import static org.codehaus.jackson.annotate.JsonSubTypes.Type;

/**
 * This class represents predicate expression and the implementation
 *
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 1/21/2015
*/
@JsonSubTypes({
        @Type(value=IntegerPredicate.class, name="integer"),
        @Type(value=StringPredicate.class, name = "string")})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class ValuePredicate<T> {
    private final PredicateEnum predicate;
    private final Comparable<T> value;

    public ValuePredicate(PredicateEnum predicate,
                          Comparable<T> value) {
        this.predicate = predicate;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" +
                "predicate=" + predicate +
                ", value=" + value +
                ']';
    }

    public PredicateEnum getPredicate() {
        return predicate;
    }

    public Comparable<T> getValue() {
        return value;
    }

    public boolean satisfies(T value) {
        int comparison = this.value.compareTo(value);
        switch (predicate) {
            case EQ:
                return this.value.equals(value);
            case NE:
                return !this.value.equals(value);
            case GT:
                return comparison == -1;
            case GE:
                return comparison == -1 || comparison == 0;
            case LT:
                return comparison == 1;
            case LE:
                return comparison == 1 || comparison == 0;
            default:
                return false;
        }
    }
}
