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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weatherAlarm.model.WeatherAlarm;

import java.io.File;
import java.io.IOException;
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
    private static final Logger logger = LoggerFactory.getLogger(SimpleAlarmService.class);
    private final Map<String, WeatherAlarm> weatherAlarmMap = new ConcurrentHashMap<>();
    private final IConfigService configService;

    @Inject
    public SimpleAlarmService(IConfigService configService) {
        this.configService = configService;
        addDefaultAlarms();
    }

    private void addDefaultAlarms() {
        final String alarmServiceInit = configService.getConfigValue(IConfigService.CONFIG_INITIAL_ALARMS);
        if (alarmServiceInit == null) {
            logger.debug("No initialization file supplied. Not adding alarm(s)...");
            return;
        }
        File configFile = new File(alarmServiceInit);
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            List<WeatherAlarm> alarms = mapper.readValue(configFile, new TypeReference<List<WeatherAlarm>>(){});
            alarms.stream().forEach(this::addAlarm);
        } catch (IOException e) {
            logger.error("Could not load alarms from file " + alarmServiceInit, e);
        }
    }

    @Override
    public List<WeatherAlarm> getAlarms() {
        return new ArrayList<>(weatherAlarmMap.values());
    }

    @Override
    public WeatherAlarm getAlarm(String name) {
        return weatherAlarmMap.get(name);
    }

    @Override
    public boolean addAlarm(WeatherAlarm alarm) {
        return weatherAlarmMap.putIfAbsent(alarm.getName(), alarm) == null;
    }

    @Override
    public boolean removeAlarm(String name) {
        return weatherAlarmMap.remove(name) != null;
    }
}
