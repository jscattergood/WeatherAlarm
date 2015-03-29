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

package weatherAlarm;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.netflix.governator.guice.LifecycleInjectorBuilderSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weatherAlarm.events.IEventStream;
import weatherAlarm.events.SubjectEventStream;
import weatherAlarm.handlers.AlarmFilterHandler;
import weatherAlarm.handlers.EmailNotificationHandler;
import weatherAlarm.handlers.WUndergroundWeatherQueryHandler;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.IWeatherAlarmService;
import weatherAlarm.services.PropertyConfigService;
import weatherAlarm.services.SimpleAlarmService;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the Guice module that configures the application.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/9/2015
 */
public class WeatherAlarmModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(WeatherAlarmModule.class);

    private final List<Object> injectees = new ArrayList<>();

    public WeatherAlarmModule(List<?> injectees) {
        this.injectees.addAll(injectees);
    }

    public LifecycleInjectorBuilderSuite asSuite() {
        return builder -> builder.withAdditionalModules(this);
    }

    @Override
    protected void configure() {
        bind(IConfigService.class).to(PropertyConfigService.class);
        bind(IWeatherAlarmService.class).to(SimpleAlarmService.class);
        bind(IEventStream.class).to(SubjectEventStream.class);
        bind(WUndergroundWeatherQueryHandler.class);
        bind(AlarmFilterHandler.class);
        bind(EmailNotificationHandler.class);

        requestInjections();
    }

    private void requestInjections() {
        injectees.forEach(this::requestInjection);
    }

    @Provides
    @Singleton
    SubjectEventStream providesEventStream() {
        SubjectEventStream events = new SubjectEventStream();
        events.observe().doOnNext(event -> logger.info(event.toString())).subscribe();
        return events;
    }
}
