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

import weatherAlarm.util.PredicateEnum;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

/**
 * This is the core model class for weather alarms.  It contains the user information and the alarm criteria.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/4/2015
 */
public class WeatherAlarm {
    private String username;
    private String emailAddress;
    private Map<WeatherDataEnum, ValuePredicate> criteria = new EnumMap<>(WeatherDataEnum.class);
    private Instant lastNotification;
    private boolean triggered;

    public WeatherAlarm(String username, String emailAddress) {
        this.username = username;
        this.emailAddress = emailAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public ValuePredicate<?> getCriteria(WeatherDataEnum weatherDataEnum) {
        return criteria.get(weatherDataEnum);
    }

    public void setCriteria(WeatherDataEnum weatherDataEnum, ValuePredicate<?> valuePredicate) {
        criteria.put(weatherDataEnum, valuePredicate);
    }

    public Instant getLastNotification() {
        return lastNotification;
    }

    public void setLastNotification(Instant lastNotification) {
        this.lastNotification = lastNotification;
        this.triggered = true;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public boolean matchesCriteria(WeatherDataEnum weatherDataEnum, Comparable value) {
        ValuePredicate valuePredicate = getCriteria(weatherDataEnum);
        //noinspection unchecked
        return valuePredicate != null &&
                valuePredicate.satisfies(value);
    }

    public boolean shouldSendNotification() {
        return !triggered;
    }

    @Override
    public String toString() {
        return "WeatherAlarm[" +
                "username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", criteria=" + criteria +
                ", lastNotification=" + lastNotification +
                ", triggered=" + triggered +
                ']';
    }

    public static class ValuePredicate<T> {
        private PredicateEnum predicate;
        private Comparable<T> value;

        public ValuePredicate(PredicateEnum predicate, Comparable<T> value) {
            this.predicate = predicate;
            this.value = value;
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
}
