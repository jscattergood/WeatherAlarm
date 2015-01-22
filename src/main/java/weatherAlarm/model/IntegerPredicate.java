package weatherAlarm.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import weatherAlarm.util.PredicateEnum;

/**
 * This class represents an integer value predicate
 *
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 1/21/2015
 */
public class IntegerPredicate extends ValuePredicate<Integer> {
    @JsonCreator
    public IntegerPredicate(@JsonProperty("predicate") PredicateEnum predicate,
                            @JsonProperty("value") Integer value) {
        super(predicate, value);
    }
}
