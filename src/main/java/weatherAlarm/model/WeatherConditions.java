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

import java.util.EnumMap;
import java.util.Map;

/**
 * @author <a href="mailto:john.scattergood@gmail.com">John Scattergood</a> 1/4/2015
 */
public class WeatherConditions {
    private Map<WeatherDataEnum, Object> weatherData = new EnumMap<>(WeatherDataEnum.class);

    public void setTemperature(Integer temp) {
        weatherData.put(WeatherDataEnum.TEMPERATURE, temp);
    }

    public Integer getTemperature() {
        return (Integer) weatherData.get(WeatherDataEnum.TEMPERATURE);
    }

    @Override
    public String toString() {
        return "WeatherConditions[temperature=" + getTemperature() + "]";
    }

}
