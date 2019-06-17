/*
 * Copyright 2019 John Scattergood
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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import weatherAlarm.util.PredicateEnum;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;

/**
 * This class represents predicate expression and the implementation
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/21/2015
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
