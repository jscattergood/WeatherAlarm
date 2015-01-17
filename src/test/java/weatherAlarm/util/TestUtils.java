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

package weatherAlarm.util;

import org.easymock.EasyMock;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.model.WeatherDataEnum;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.IWeatherAlarmService;
import weatherAlarm.services.SimpleAlarmService;

/**
 * Utility class for reusable test scaffolding
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/11/2015
 */
public class TestUtils {
    private TestUtils() {

    }

    public static IWeatherAlarmService getMockAlarmService() {
        WeatherAlarm alarm = createWeatherAlarm();
        IWeatherAlarmService alarmService = new SimpleAlarmService();
        alarmService.addAlarm(alarm);
        return alarmService;
    }

    public static IWeatherAlarmService getEmptyAlarmService() {
        return new SimpleAlarmService();
    }

    public static WeatherAlarm createWeatherAlarm() {
        WeatherAlarm alarm = new WeatherAlarm("zero degrees");
        alarm.setEmailAddress("joe@xyz.com");
        WeatherAlarm.ValuePredicate<Integer> valuePredicate = new WeatherAlarm.ValuePredicate<>(PredicateEnum.GT, 0);
        alarm.setCriteria(WeatherDataEnum.TEMPERATURE, valuePredicate);
        alarm.setLocation("99999");
        return alarm;
    }

    public static IConfigService getMockConfigService() {
        IConfigService configService = EasyMock.createNiceMock(IConfigService.class);
        EasyMock.replay(configService);
        return configService;
    }

    public static IConfigService getMockConfigService(String config, String value) {
        IConfigService configService = EasyMock.createNiceMock(IConfigService.class);
        EasyMock.expect(configService.getConfigValue(config)).andReturn(value).anyTimes();
        EasyMock.replay(configService);
        return configService;
    }

    public static String getMockJsonResult() {
        return "{\"query\":{" +
                "\"count\":1," +
                "\"created\":\"2015-01-12T01:12:22Z\"," +
                "\"lang\":\"en-US\"," +
                "\"results\":{" +
                "\"channel\":{" +
                "\"item\":{" +
                "\"condition\":{" +
                "\"code\":\"34\"," +
                "\"date\":\"Sun, 11 Jan 2015 4:52 pm PST\"," +
                "\"temp\":\"56\"," +
                "\"text\":\"Fair\"}}}}}}";
    }
}
