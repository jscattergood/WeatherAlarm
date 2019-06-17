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

/**
 * This interface represents a class that provides the application's configuration.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/9/2015
 */
public interface IConfigService {
    String CONFIG_EMAIL_HOST_NAME = "weatherAlarm.emailHostName";
    String CONFIG_EMAIL_AUTH_USER = "weatherAlarm.emailAuthUser";
    String CONFIG_EMAIL_AUTH_PASS = "weatherAlarm.emailAuthPass";
    String CONFIG_INITIAL_ALARMS = "weatherAlarm.initialAlarms";
    String CONFIG_WEATHER_SERVICE_QUERY_INTERVAL = "weatherAlarm.weatherServiceQueryInterval";
    String CONFIG_WEATHER_SERVICE_API_KEY = "weatherAlarm.weatherServiceApiKey";

    /**
     * Gets the configuration
     *
     * @param config the configuration key
     * @return the configuration value or {@code null}
     */
    String getConfigValue(String config);
}
