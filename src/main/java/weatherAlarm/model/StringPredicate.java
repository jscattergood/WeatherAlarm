/*
 * Copyright 2015 John Scattergood
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package weatherAlarm.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import weatherAlarm.util.PredicateEnum;

/**
 * This class represents a string predicate.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/21/2015
 */
public class StringPredicate extends ValuePredicate<String> {
    @JsonCreator
    public StringPredicate(@JsonProperty("predicate") PredicateEnum predicate,
                           @JsonProperty("value") String value) {
        super(predicate, value);
    }

}
