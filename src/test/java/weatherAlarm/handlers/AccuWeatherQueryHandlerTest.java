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

package weatherAlarm.handlers;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 3/28/2015
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AccuWeatherQueryHandler.class)
public class AccuWeatherQueryHandlerTest extends AbstractWeatherQueryHandlerTest {
    @Override
    protected Class<? extends AbstractWeatherQueryHandler> getHandlerClass() {
        return AccuWeatherQueryHandler.class;
    }

    @Override
    protected String getMockJsonResult() {
        return "[\n" +
                "  {\n" +
                "    \"LocalObservationDateTime\": \"2020-04-22T14:00:00+08:00\",\n" +
                "    \"EpochTime\": 1587535200,\n" +
                "    \"WeatherText\": \"Cloudy\",\n" +
                "    \"WeatherIcon\": 7,\n" +
                "    \"HasPrecipitation\": false,\n" +
                "    \"PrecipitationType\": null,\n" +
                "    \"LocalSource\": {\n" +
                "      \"Id\": 7,\n" +
                "      \"Name\": \"Huafeng\",\n" +
                "      \"WeatherCode\": \"01\"\n" +
                "    },\n" +
                "    \"IsDayTime\": true,\n" +
                "    \"Temperature\": {\n" +
                "      \"Metric\": {\n" +
                "        \"Value\": 18.6,\n" +
                "        \"Unit\": \"C\",\n" +
                "        \"UnitType\": 17\n" +
                "      },\n" +
                "      \"Imperial\": {\n" +
                "        \"Value\": 66.0,\n" +
                "        \"Unit\": \"F\",\n" +
                "        \"UnitType\": 18\n" +
                "      }\n" +
                "    },\n" +
                "    \"MobileLink\": \"http://m.accuweather.com/en/cn/daming-town/94598/current-weather/94598?lang=en-us\",\n" +
                "    \"Link\": \"http://www.accuweather.com/en/cn/daming-town/94598/current-weather/94598?lang=en-us\"\n" +
                "  }\n" +
                "]";
    }
}
