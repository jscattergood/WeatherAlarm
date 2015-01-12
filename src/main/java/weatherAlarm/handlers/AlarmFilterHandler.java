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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;
import weatherAlarm.events.*;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.model.WeatherConditions;
import weatherAlarm.model.WeatherDataEnum;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.IWeatherAlarmService;
import weatherAlarm.util.PredicateEnum;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is responsible for filtering current weather conditions to determining if an alarm should be raised
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 12/30/2014
 */
@Singleton
public class AlarmFilterHandler extends EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AlarmFilterHandler.class);
    private final IWeatherAlarmService alarmService;
    private final IConfigService configService;

    @Inject
    public AlarmFilterHandler(IEventStream stream,
                              IConfigService configService,
                              IWeatherAlarmService weatherAlarmService) {
        super(stream);
        this.alarmService = weatherAlarmService;
        this.configService = configService;
        addDefaultAlarm();

        final Observable<IEvent> observableFilterEvent = eventStream
                .observe(WeatherConditionEvent.class)
                .flatMap(evaluateEvent());
        eventStream.publish(observableFilterEvent);

        final Observable<IEvent> observableNotificationEvent = eventStream
                .observe(NotificationSentEvent.class)
                .flatMap(handleNotification());
        eventStream.publish(observableNotificationEvent);

        final Observable<IEvent> observableFilterNotMatchEvent = eventStream
                .observe(FilterNoMatchEvent.class)
                .flatMap(handleFilterNoMatch());
        eventStream.publish(observableFilterNotMatchEvent);
    }

    private void addDefaultAlarm() {
        final String userName = configService.getConfigValue(IConfigService.CONFIG_USER_NAME);
        if (userName == null) {
            logger.debug("No user defined. Not adding alarm...");
            return;
        }
        final String userEmail = configService.getConfigValue(IConfigService.CONFIG_USER_EMAIL);
        if (userEmail == null) {
            logger.debug("No user email defined. Not adding alarm...");
            return;
        }
        final String location = configService.getConfigValue(IConfigService.CONFIG_LOCATION);
        if (location == null) {
            logger.debug("No weather location defined. Not adding alarm...");
            return;
        }
        final String temperaturePredicate = configService.getConfigValue(IConfigService.CONFIG_TEMPERATURE_PREDICATE);
        final PredicateEnum predicateEnum = PredicateEnum.valueOf(temperaturePredicate);
        if (predicateEnum == null) {
            logger.debug("Invalid predicate enum " + temperaturePredicate + ". Not adding alarm...");
            return;
        }
        final String temperatureValue = configService.getConfigValue(IConfigService.CONFIG_TEMPERATURE_VALUE);
        final Integer value;
        try {
            value = Integer.parseInt(temperatureValue);
        } catch (NumberFormatException e) {
            logger.debug("Invalid temperature value " + temperatureValue + ". Not adding alarm...");
            return;
        }

        WeatherAlarm alarm = new WeatherAlarm(userName, userEmail);
        alarm.setLocation(location);
        WeatherAlarm.ValuePredicate<Integer> predicate = new WeatherAlarm.ValuePredicate<>(predicateEnum, value);
        alarm.setCriteria(WeatherDataEnum.TEMPERATURE, predicate);
        alarmService.addAlarm(alarm);
    }

    private Func1<? super WeatherConditionEvent, ? extends Observable<IEvent>> evaluateEvent() {
        return event -> {
            WeatherConditions conditions = event.getConditions();
            List<IEvent> eventList = alarmService.getAlarms().stream()
                    .map(convertToEvent(conditions))
                    .collect(Collectors.toList());
            return Observable.from(eventList);
        };
    }

    private Function<WeatherAlarm, IEvent> convertToEvent(WeatherConditions conditions) {
        return alarm -> {
            boolean match = alarm.matchesCriteria(WeatherDataEnum.TEMPERATURE, conditions.getTemperature());
            boolean shouldSend = alarm.shouldSendNotification();
            if (match && shouldSend) {
                return new FilterMatchEvent(alarm, conditions);
            } else if (match) {
                return new FilterNoMatchEvent(FilterNoMatchEvent.Reason.NOT_READY, alarm);
            } else {
                return new FilterNoMatchEvent(FilterNoMatchEvent.Reason.CRITERIA, alarm);
            }
        };
    }

    private Func1<? super NotificationSentEvent, ? extends Observable<IEvent>> handleNotification() {
        return event -> {
            WeatherAlarm alarm = event.getAlarm();
            alarm.setLastNotification(event.getEventTime());
            return Observable.just(new WeatherAlarmUpdatedEvent(alarm));
        };
    }

    private Func1<? super FilterNoMatchEvent, ? extends Observable<IEvent>> handleFilterNoMatch() {
        return event -> {
            WeatherAlarm alarm = event.getAlarm();
            // Reset the alarm so that it can be re-triggered if criteria met later
            if (alarm.isTriggered() && FilterNoMatchEvent.Reason.CRITERIA.equals(event.getReason())) {
                alarm.setTriggered(false);
                return Observable.just(new WeatherAlarmUpdatedEvent(alarm));
            } else {
                return Observable.empty();
            }
        };
    }
}
