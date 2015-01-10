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
import com.netflix.governator.guice.LifecycleInjectorBuilderSuite;
import weatherAlarm.events.IEventStream;
import weatherAlarm.events.SubjectEventStream;
import weatherAlarm.handlers.AlarmFilterHandler;
import weatherAlarm.handlers.EmailNotificationHandler;
import weatherAlarm.handlers.WeatherQueryHandler;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.PropertyConfigService;

/**
 * This class is the Guice module that configures the application.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/9/2015
 */
public class WeatherAlarmModule extends AbstractModule {
    public static LifecycleInjectorBuilderSuite asSuite() {
        return builder -> builder.withAdditionalModules(new WeatherAlarmModule());
    }

    @Override
    protected void configure() {
        bind(IConfigService.class).to(PropertyConfigService.class).asEagerSingleton();
        bind(IEventStream.class).to(SubjectEventStream.class).asEagerSingleton();
        bind(WeatherQueryHandler.class).asEagerSingleton();
        bind(AlarmFilterHandler.class).asEagerSingleton();
        bind(EmailNotificationHandler.class).asEagerSingleton();
    }

    @Provides
    SubjectEventStream providesEventStream() {
        SubjectEventStream events = new SubjectEventStream();
        events.observe().doOnNext(System.out::println).subscribe();
        return events;
    }
}
