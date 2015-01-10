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

package weatherAlarm.services;

import com.google.inject.Singleton;
import weatherAlarm.model.WeatherAlarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is a simple map backed implementation of {@link weatherAlarm.services.IWeatherAlarmService}
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/10/2015
 */
@Singleton
public class SimpleAlarmService implements IWeatherAlarmService {
    private final Map<String, WeatherAlarm> weatherAlarmMap = new ConcurrentHashMap<>();

    @Override
    public List<WeatherAlarm> getAlarms() {
        return new ArrayList<>(weatherAlarmMap.values());
    }

    @Override
    public WeatherAlarm getAlarm(String userEmail) {
        return weatherAlarmMap.get(userEmail);
    }

    @Override
    public boolean addAlarm(WeatherAlarm alarm) {
        return weatherAlarmMap.putIfAbsent(alarm.getEmailAddress(), alarm) == null;
    }

    @Override
    public boolean removeAlarm(String userEmail) {
        return weatherAlarmMap.remove(userEmail) != null;
    }
}
