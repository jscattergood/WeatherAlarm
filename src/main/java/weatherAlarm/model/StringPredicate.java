package weatherAlarm.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import weatherAlarm.util.PredicateEnum;

/**
 * This class represents a string predicate.
 *
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 1/21/2015
 */
public class StringPredicate extends ValuePredicate<String> {
    @JsonCreator
    public StringPredicate(@JsonProperty("predicate") PredicateEnum predicate,
                           @JsonProperty("value") String value) {
        super(predicate, value);
    }

}
