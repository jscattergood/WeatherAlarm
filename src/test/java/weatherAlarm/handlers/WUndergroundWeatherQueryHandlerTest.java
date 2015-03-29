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

package weatherAlarm.handlers;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 3/28/2015
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(WUndergroundWeatherQueryHandler.class)
public class WUndergroundWeatherQueryHandlerTest extends AbstractWeatherQueryHandlerTest {
    @Override
    protected Class<? extends AbstractWeatherQueryHandler> getHandlerClass() {
        return WUndergroundWeatherQueryHandler.class;
    }

    @Override
    protected String getMockJsonResult() {
        return "\n" +
                "{\n" +
                "  \"response\": {\n" +
                "  \"version\":\"0.1\",\n" +
                "  \"termsofService\":\"http://www.wunderground.com/weather/api/d/terms.html\",\n" +
                "  \"features\": {\n" +
                "  \"conditions\": 1\n" +
                "  }\n" +
                "\t}\n" +
                "  ,\t\"current_observation\": {\n" +
                "\t\t\"image\": {\n" +
                "\t\t\"url\":\"http://icons.wxug.com/graphics/wu2/logo_130x80.png\",\n" +
                "\t\t\"title\":\"Weather Underground\",\n" +
                "\t\t\"link\":\"http://www.wunderground.com\"\n" +
                "\t\t},\n" +
                "\t\t\"display_location\": {\n" +
                "\t\t\"full\":\"San Francisco, CA\",\n" +
                "\t\t\"city\":\"San Francisco\",\n" +
                "\t\t\"state\":\"CA\",\n" +
                "\t\t\"state_name\":\"California\",\n" +
                "\t\t\"country\":\"US\",\n" +
                "\t\t\"country_iso3166\":\"US\",\n" +
                "\t\t\"zip\":\"94101\",\n" +
                "\t\t\"magic\":\"1\",\n" +
                "\t\t\"wmo\":\"99999\",\n" +
                "\t\t\"latitude\":\"37.77500916\",\n" +
                "\t\t\"longitude\":\"-122.41825867\",\n" +
                "\t\t\"elevation\":\"47.00000000\"\n" +
                "\t\t},\n" +
                "\t\t\"observation_location\": {\n" +
                "\t\t\"full\":\"SOMA - Near Van Ness, San Francisco, California\",\n" +
                "\t\t\"city\":\"SOMA - Near Van Ness, San Francisco\",\n" +
                "\t\t\"state\":\"California\",\n" +
                "\t\t\"country\":\"US\",\n" +
                "\t\t\"country_iso3166\":\"US\",\n" +
                "\t\t\"latitude\":\"37.773285\",\n" +
                "\t\t\"longitude\":\"-122.417725\",\n" +
                "\t\t\"elevation\":\"49 ft\"\n" +
                "\t\t},\n" +
                "\t\t\"estimated\": {\n" +
                "\t\t},\n" +
                "\t\t\"station_id\":\"KCASANFR58\",\n" +
                "\t\t\"observation_time\":\"Last Updated on March 28, 3:00 PM PDT\",\n" +
                "\t\t\"observation_time_rfc822\":\"Sat, 28 Mar 2015 15:00:10 -0700\",\n" +
                "\t\t\"observation_epoch\":\"1427580010\",\n" +
                "\t\t\"local_time_rfc822\":\"Sat, 28 Mar 2015 15:00:42 -0700\",\n" +
                "\t\t\"local_epoch\":\"1427580042\",\n" +
                "\t\t\"local_tz_short\":\"PDT\",\n" +
                "\t\t\"local_tz_long\":\"America/Los_Angeles\",\n" +
                "\t\t\"local_tz_offset\":\"-0700\",\n" +
                "\t\t\"weather\":\"Clear\",\n" +
                "\t\t\"temperature_string\":\"74.4 F (23.6 C)\",\n" +
                "\t\t\"temp_f\":74.4,\n" +
                "\t\t\"temp_c\":23.6,\n" +
                "\t\t\"relative_humidity\":\"43%\",\n" +
                "\t\t\"wind_string\":\"From the WSW at 8.0 MPH Gusting to 8.0 MPH\",\n" +
                "\t\t\"wind_dir\":\"WSW\",\n" +
                "\t\t\"wind_degrees\":253,\n" +
                "\t\t\"wind_mph\":8.0,\n" +
                "\t\t\"wind_gust_mph\":\"8.0\",\n" +
                "\t\t\"wind_kph\":12.9,\n" +
                "\t\t\"wind_gust_kph\":\"12.9\",\n" +
                "\t\t\"pressure_mb\":\"1020\",\n" +
                "\t\t\"pressure_in\":\"30.12\",\n" +
                "\t\t\"pressure_trend\":\"-\",\n" +
                "\t\t\"dewpoint_string\":\"50 F (10 C)\",\n" +
                "\t\t\"dewpoint_f\":50,\n" +
                "\t\t\"dewpoint_c\":10,\n" +
                "\t\t\"heat_index_string\":\"NA\",\n" +
                "\t\t\"heat_index_f\":\"NA\",\n" +
                "\t\t\"heat_index_c\":\"NA\",\n" +
                "\t\t\"windchill_string\":\"NA\",\n" +
                "\t\t\"windchill_f\":\"NA\",\n" +
                "\t\t\"windchill_c\":\"NA\",\n" +
                "\t\t\"feelslike_string\":\"74.4 F (23.6 C)\",\n" +
                "\t\t\"feelslike_f\":\"74.4\",\n" +
                "\t\t\"feelslike_c\":\"23.6\",\n" +
                "\t\t\"visibility_mi\":\"10.0\",\n" +
                "\t\t\"visibility_km\":\"16.1\",\n" +
                "\t\t\"solarradiation\":\"--\",\n" +
                "\t\t\"UV\":\"6\",\"precip_1hr_string\":\"0.00 in ( 0 mm)\",\n" +
                "\t\t\"precip_1hr_in\":\"0.00\",\n" +
                "\t\t\"precip_1hr_metric\":\" 0\",\n" +
                "\t\t\"precip_today_string\":\"0.00 in (0 mm)\",\n" +
                "\t\t\"precip_today_in\":\"0.00\",\n" +
                "\t\t\"precip_today_metric\":\"0\",\n" +
                "\t\t\"icon\":\"clear\",\n" +
                "\t\t\"icon_url\":\"http://icons.wxug.com/i/c/k/clear.gif\",\n" +
                "\t\t\"forecast_url\":\"http://www.wunderground.com/US/CA/San_Francisco.html\",\n" +
                "\t\t\"history_url\":\"http://www.wunderground.com/weatherstation/WXDailyHistory.asp?ID=KCASANFR58\",\n" +
                "\t\t\"ob_url\":\"http://www.wunderground.com/cgi-bin/findweather/getForecast?query=37.773285,-122.417725\",\n" +
                "\t\t\"nowcast\":\"\"\n" +
                "\t}\n" +
                "}\n";
    }
}
