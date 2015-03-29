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

import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import weatherAlarm.events.IEvent;
import weatherAlarm.events.IEventStream;
import weatherAlarm.events.WeatherConditionEvent;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.IWeatherAlarmService;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * This class is the base class for all weather query handlers.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 3/28/2015
 */
public abstract class AbstractWeatherQueryHandler extends EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractWeatherQueryHandler.class);
    private static final long SECS_PER_MIN = 60;
    protected static final long DEFAULT_QUERY_INTERVAL = 15 * SECS_PER_MIN;
    protected final IWeatherAlarmService alarmService;
    private final long queryInterval;
    protected HttpResourceGroup cachedResourceGroup;
    protected HttpRequestTemplate<ByteBuf> cachedRequestTemplate;
    private Observable<Long> intervalObservable;

    public AbstractWeatherQueryHandler(IEventStream stream, IConfigService configService, IWeatherAlarmService weatherAlarmService) {
        super(stream);
        this.alarmService = weatherAlarmService;

        final String intervalProperty = configService.getConfigValue(IConfigService.CONFIG_WEATHER_SERVICE_QUERY_INTERVAL);
        if (intervalProperty != null) {
            queryInterval = Long.valueOf(intervalProperty);
        } else {
            logger.error("No query interval defined. Using default " + DEFAULT_QUERY_INTERVAL + " seconds");
            queryInterval = DEFAULT_QUERY_INTERVAL;
        }
    }

    protected final void finishInit() {
        if (intervalObservable != null) {
            logger.error("handler is already initialized!");
            return;
        }
        intervalObservable = Observable.interval(queryInterval, TimeUnit.SECONDS,
                Schedulers.newThread());
        intervalObservable.forEach(inLong -> requestWeatherData());
    }

    protected void requestWeatherData() {
        List<String> locations = alarmService.getAlarms().stream()
                .map(WeatherAlarm::getLocation)
                .collect(Collectors.toList());
        Observable<IEvent> event = Observable.from(locations)
                .flatMap(location -> buildRequest(location)
                        .observe()
                        .map(mapJsonToEvent()));
        eventStream.publish(event);
    }

    private RibbonRequest<ByteBuf> buildRequest(String location) {
        HttpRequestTemplate<ByteBuf> weatherQueryTemplate = getRequestTemplate();
        return weatherQueryTemplate.requestBuilder()
                .withRequestProperty("location", location)
                .build();
    }

    protected abstract HttpRequestTemplate<ByteBuf> getRequestTemplate();

    protected abstract HttpResourceGroup getResourceGroup();

    protected Func1<ByteBuf, WeatherConditionEvent> mapJsonToEvent() {
        return byteBuf -> {
            final String jsonString = byteBuf.toString(Charset.defaultCharset());
            logger.debug("Received response:" + jsonString);
            return createWeatherConditionEvent(jsonString);
        };
    }

    protected abstract WeatherConditionEvent createWeatherConditionEvent(String jsonString);
}
