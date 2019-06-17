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

package weatherAlarm.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weatherAlarm.model.WeatherConditions;

/**
 * This event is used to signal that new {@link weatherAlarm.model.WeatherConditions} have been observed.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/3/2015
 */
public class WeatherConditionEvent implements IEvent {
    public static final Logger logger = LoggerFactory.getLogger(WeatherConditionEvent.class);
    private WeatherConditions conditions;

    public WeatherConditionEvent(WeatherConditions conditions) {
        this.conditions = conditions;
    }

    public WeatherConditions getConditions() {
        return conditions;
    }

    @Override
    public String toString() {
        return "WeatherConditionEvent[conditions=" + getConditions() + "]";
    }
}
