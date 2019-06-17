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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weatherAlarm.events.IEventStream;
import weatherAlarm.events.WeatherConditionEvent;
import weatherAlarm.model.WeatherConditions;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.IWeatherAlarmService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * This class is responsible for querying the Yahoo weather service for the current conditions
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 12/28/2014
 */
@Deprecated
@Singleton
public class YahooWeatherQueryHandler extends AbstractWeatherQueryHandler {
    private static final Logger logger = LoggerFactory.getLogger(YahooWeatherQueryHandler.class);

    @Inject
    public YahooWeatherQueryHandler(IEventStream stream,
                                    IConfigService configService,
                                    IWeatherAlarmService weatherAlarmService) {
        super(stream, configService, weatherAlarmService);
        finishInit();
    }

    @Override
    protected HttpRequestTemplate<ByteBuf> getRequestTemplate() {
        if (cachedRequestTemplate == null) {
            HttpResourceGroup group = getResourceGroup();
            String encodedYql = StringUtils.EMPTY;
            String encodedEnv = StringUtils.EMPTY;
            try {
                encodedYql = URLEncoder.encode("select item.condition from weather.forecast where location = ", "UTF-8");
                encodedEnv = URLEncoder.encode("store://datatables.org/alltableswithkeys", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
            }
            cachedRequestTemplate = group.newTemplateBuilder("getWeatherByLocation")
                    .withMethod("GET")
                    .withUriTemplate("/v1/public/yql" +
                            "?q=" + encodedYql + "{location}" +
                            "&format=json" +
                            "&env=" + encodedEnv)
                    .build();
        }
        return cachedRequestTemplate;
    }

    @Override
    protected HttpResourceGroup getResourceGroup() {
        if (cachedResourceGroup == null) {
            cachedResourceGroup = Ribbon.createHttpResourceGroup("yahooWeatherService",
                    ClientOptions.create()
                            .withMaxAutoRetries(3)
                            .withConfigurationBasedServerList("query.yahooapis.com"));
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
                            .get("query")
                            .get("results")
                            .get("channel")
                            .get("item")
                            .get("condition")
                            .get("temp").asInt()
            );
            return new WeatherConditionEvent(conditions);
        } catch (IOException e) {
            logger.error("Could not create WeatherConditionEvent from JSON string", e);
            throw new RuntimeException(e);
        }
    }
}
