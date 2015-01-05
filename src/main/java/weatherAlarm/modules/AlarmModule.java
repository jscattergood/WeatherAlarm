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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;
import weatherAlarm.events.*;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.model.WeatherConditions;
import weatherAlarm.model.WeatherDataEnum;
import weatherAlarm.util.PredicateEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is responsible for filtering current weather conditions to determining if an alarm should be raised
 *
 * @author <a href="mailto:john.scattergood@gmail.com">John Scattergood</a> 12/30/2014
 */
public class AlarmModule extends EventModule {
    private List<WeatherAlarm> alarms = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(AlarmModule.class);

    public AlarmModule(EventStream stream) {
        super(stream);
    }

    @Override
    protected void configure() {
        addAlarms();

        final Observable<IModuleEvent> observableEvent = eventStream
                .observe(WeatherConditionEvent.class)
                .flatMap(evaluateEvent());
        eventStream.publish(observableEvent);
    }

    private void addAlarms() {
        final String userName = System.getProperty("weatherAlarm.userName");
        if (userName == null) {
            logger.error("No user defined. Not adding alarm...");
            return;
        }
        final String userEmail = System.getProperty("weatherAlarm.userEmail");
        if (userEmail == null) {
            logger.error("No user email defined. Not adding alarm...");
            return;
        }
        final String temperaturePredicate = System.getProperty("weatherAlarm.temperaturePredicate");
        final PredicateEnum predicateEnum = PredicateEnum.valueOf(temperaturePredicate);
        if (predicateEnum == null) {
            logger.error("Invalid predicate enum " + temperaturePredicate +". Not adding alarm...");
            return;
        }
        final String temperatureValue = System.getProperty("weatherAlarm.temperatureValue");
        final Integer value;
        try {
            value = Integer.parseInt(temperatureValue);
        }
        catch (NumberFormatException e) {
            logger.error("Invalid temperature value " + temperatureValue + ". Not adding alarm...");
            return;
        }

        WeatherAlarm alarm = new WeatherAlarm(userName, userEmail);
        WeatherAlarm.ValuePredicate<Integer> predicate = new WeatherAlarm.ValuePredicate<>(predicateEnum, value);
        alarm.setCriteria(WeatherDataEnum.TEMPERATURE, predicate);
        alarms.add(alarm);
    }

    private Func1<? super IModuleEvent, ? extends Observable<IModuleEvent>> evaluateEvent() {
        return new Func1<IModuleEvent, Observable<IModuleEvent>>() {
            @Override
            public Observable<IModuleEvent> call(IModuleEvent event) {
                if (event instanceof WeatherConditionEvent) {
                    WeatherConditionEvent weatherConditionEvent = (WeatherConditionEvent)event;
                    Stream<WeatherAlarm> matchingAlarms = alarms.stream()
                            .filter(alarm -> {
                                WeatherConditions conditions = weatherConditionEvent.getConditions();
                                return alarm.matchesCriteria(WeatherDataEnum.TEMPERATURE, conditions.getTemperature());
                            });
                    IModuleEvent[] eventArray = matchingAlarms
                            .flatMap(alarm -> Stream.of(new FilterMatchEvent()))
                            .toArray(IModuleEvent[]::new);
                    return Observable.from(eventArray);
                }
                return Observable.just(new FilterNoMatchEvent());
            }
        };
    }
}
