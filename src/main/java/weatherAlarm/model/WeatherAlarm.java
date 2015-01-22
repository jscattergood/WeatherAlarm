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

import java.time.Instant;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * This is the core model class for weather alarms.  It contains the user information and the alarm criteria.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/4/2015
 */
public class WeatherAlarm {
    private final Map<WeatherDataEnum, ValuePredicate> criteria = new EnumMap<>(WeatherDataEnum.class);
    private final String name;
    private String emailAddress;
    private String location;
    private Instant lastNotification;
    private boolean triggered;

    @JsonCreator
    public WeatherAlarm(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Map<WeatherDataEnum, ValuePredicate> getCriteria() {
        return Collections.unmodifiableMap(criteria);
    }

    public void setCriteria(Map<WeatherDataEnum, ValuePredicate> map) {
        criteria.putAll(map);
    }

    public ValuePredicate<?> getCriteriaFor(WeatherDataEnum weatherDataEnum) {
        return criteria.get(weatherDataEnum);
    }

    public void setCriteriaFor(WeatherDataEnum weatherDataEnum, ValuePredicate<?> valuePredicate) {
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
        ValuePredicate valuePredicate = getCriteriaFor(weatherDataEnum);
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
                "name='" + name + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", criteria=" + criteria +
                ", lastNotification=" + lastNotification +
                ", triggered=" + triggered +
                ']';
    }

}
