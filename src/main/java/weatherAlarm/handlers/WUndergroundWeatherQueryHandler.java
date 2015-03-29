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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weatherAlarm.events.IEventStream;
import weatherAlarm.events.WeatherConditionEvent;
import weatherAlarm.model.WeatherConditions;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.IWeatherAlarmService;

import java.io.IOException;

/**
 * This class is responsible for querying the WeatherUnderground weather service for the current conditions
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 3/28/2015
 */
@Singleton
public class WUndergroundWeatherQueryHandler extends AbstractWeatherQueryHandler {
    private static final Logger logger = LoggerFactory.getLogger(WUndergroundWeatherQueryHandler.class);
    private final String apiKey;

    @Inject
    public WUndergroundWeatherQueryHandler(IEventStream stream,
                                           IConfigService configService,
                                           IWeatherAlarmService weatherAlarmService) {
        super(stream, configService, weatherAlarmService);
        this.apiKey = configService.getConfigValue(IConfigService.CONFIG_WEATHER_SERVICE_API_KEY);
        if (this.apiKey == null) {
            logger.error("No api key defined. Cannot query weather service...");
            return;
        }
        finishInit();
    }

    @Override
    protected HttpRequestTemplate<ByteBuf> getRequestTemplate() {
        if (cachedRequestTemplate == null) {
            HttpResourceGroup group = getResourceGroup();
            cachedRequestTemplate = group.newTemplateBuilder("getWeatherByLocation")
                    .withMethod("GET")
                    .withUriTemplate("/api/" + apiKey + "/conditions/q/{location}.json")
                    .build();
        }
        return cachedRequestTemplate;
    }

    @Override
    protected HttpResourceGroup getResourceGroup() {
        if (cachedResourceGroup == null) {
            cachedResourceGroup = Ribbon.createHttpResourceGroup("wundergroundWeatherService",
                    ClientOptions.create()
                            .withMaxAutoRetries(3)
                            .withConfigurationBasedServerList("api.wunderground.com"));
        }
        return cachedResourceGroup;
    }

    @Override
    protected WeatherConditionEvent createWeatherConditionEvent(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(jsonString);
            WeatherConditions conditions = new WeatherConditions();
            conditions.setTemperature(root
                            .get("current_observation")
                            .get("temp_f").asInt()
            );
            return new WeatherConditionEvent(conditions);
        } catch (IOException e) {
            logger.error("Could not create WeatherConditionEvent from JSON string", e);
            throw new RuntimeException(e);
        }
    }
}
