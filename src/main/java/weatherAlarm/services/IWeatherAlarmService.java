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

package weatherAlarm.services;

import weatherAlarm.model.WeatherAlarm;

import java.util.List;

/**
 * This interface represents a class that stores and retrieves {@link weatherAlarm.model.WeatherAlarm} instances.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/10/2015
 */
public interface IWeatherAlarmService {
    /**
     * Gets all alarms managed by the service
     *
     * @return list of alarms
     */
    List<WeatherAlarm> getAlarms();

    /**
     * Returns an alarm if present
     *
     * @param name unique alarm name
     * @return matching alarm or {@code null}
     */
    WeatherAlarm getAlarm(String name);

    /**
     * Adds an alarm. Implementation should be idempotent.
     *
     * @param alarm the alarm
     * @return true if the alarm was added, false if the alarm was already present
     */
    boolean addAlarm(WeatherAlarm alarm);

    /**
     * Removes an alarm.  Implementation should be idempotent.
     *
     * @param name unique alarm name
     * @return true if the alarm was removed, false if the alarm was not present
     */
    boolean removeAlarm(String name);
}
