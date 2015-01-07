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

package weatherAlarm.modules;

import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import weatherAlarm.events.EventStream;
import weatherAlarm.events.IModuleEvent;
import weatherAlarm.events.WeatherConditionEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for querying the Weather Service for the current conditions
 *
 * @author <a href="mailto:john.scattergood@gmail.com">John Scattergood</a> 12/28/2014
 */
public class WeatherQueryModule extends EventModule {
    private static final Logger logger = LoggerFactory.getLogger(WeatherQueryModule.class);
    private static final long SECS_PER_MIN = 60;
    private static final long DEFAULT_QUERY_INTERVAL = 15 * SECS_PER_MIN;

    private String location;
    private HttpResourceGroup cachedResourceGroup;
    private HttpRequestTemplate<ByteBuf> cachedRequestTemplate;

    public WeatherQueryModule(EventStream stream) {
        super(stream);
    }

    @Override
    protected void configure() {
        location = System.getProperty("weatherAlarm.location");
        if (location == null) {
            logger.error("No location defined");
        }
        final String intervalProperty = System.getProperty("weatherAlarm.weatherServiceQueryInterval");
        long queryInterval;
        if (intervalProperty != null) {
            queryInterval = Long.valueOf(intervalProperty);
        } else {
            logger.error("No query interval defined. Using default " + DEFAULT_QUERY_INTERVAL + " seconds");
            queryInterval = DEFAULT_QUERY_INTERVAL;
        }

        Observable.interval(queryInterval, TimeUnit.SECONDS,
                Schedulers.newThread()).forEach(inLong -> requestWeatherData());
    }

    private void requestWeatherData() {
        RibbonRequest<ByteBuf> request = buildRequest();
        final Observable<IModuleEvent> event = request
                .observe()
                .map(mapJsonToEvent());
        eventStream.publish(event);
    }

    private RibbonRequest<ByteBuf> buildRequest() {
        HttpRequestTemplate<ByteBuf> weatherQueryTemplate = getRequestTemplate();
        return weatherQueryTemplate.requestBuilder()
                .withRequestProperty("location", location)
                .build();
    }

    private HttpRequestTemplate<ByteBuf> getRequestTemplate() {
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

    private HttpResourceGroup getResourceGroup() {
        if (cachedResourceGroup == null) {
            cachedResourceGroup = Ribbon.createHttpResourceGroup("yahooWeatherService",
                    ClientOptions.create()
                            .withMaxAutoRetries(3)
                            .withConfigurationBasedServerList("query.yahooapis.com"));
        }
        return cachedResourceGroup;
    }

    private Func1<ByteBuf, WeatherConditionEvent> mapJsonToEvent() {
        return byteBuf -> {
            final String result = byteBuf.toString(Charset.defaultCharset());
            logger.debug("Received response:" + result);
            return new WeatherConditionEvent(result);
        };
    }
}
