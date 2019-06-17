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

import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import weatherAlarm.events.*;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.model.WeatherConditions;
import weatherAlarm.services.IWeatherAlarmService;
import weatherAlarm.util.TestUtils;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for testing {@link weatherAlarm.handlers.AlarmFilterHandler}
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/10/2015
 */
public class AlarmFilterHandlerTest {
    @Test
    public void testEvaluateFilterMatchCriteria() {
        final boolean[] received = {false};
        SubjectEventStream stream = new SubjectEventStream();
        new AlarmFilterHandler(stream, TestUtils.getMockAlarmService());
        WeatherConditions conditions = new WeatherConditions();
        conditions.setTemperature(1);
        stream.observe(FilterMatchEvent.class).forEach(event -> received[0] = true);
        stream.publish(Observable.just(new WeatherConditionEvent(conditions)));
        Assert.assertTrue("No event received", received[0]);
    }

    @Test
    public void testEvaluateFilterMultipleMatchCriteria() throws InterruptedException {
        int numberOfAlarms = 5;
        final CountDownLatch received = new CountDownLatch(numberOfAlarms);
        SubjectEventStream stream = new SubjectEventStream();
        new AlarmFilterHandler(stream, TestUtils.getMockAlarmService(numberOfAlarms));
        WeatherConditions conditions = new WeatherConditions();
        conditions.setTemperature(1);
        stream.observe(FilterMatchEvent.class).forEach(event -> received.countDown());
        stream.publish(Observable.just(new WeatherConditionEvent(conditions)));
        Assert.assertTrue("No event received", received.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testEvaluateFilterNoMatchCriteria() {
        final boolean[] received = {false};
        SubjectEventStream stream = new SubjectEventStream();
        new AlarmFilterHandler(stream, TestUtils.getMockAlarmService());
        WeatherConditions conditions = new WeatherConditions();
        conditions.setTemperature(-1);
        stream.observe(FilterNoMatchEvent.class).forEach(event -> received[0] = true);
        stream.publish(Observable.just(new WeatherConditionEvent(conditions)));
        Assert.assertTrue("No event received", received[0]);
    }

    @Test
    public void testHandleNotification() {
        final boolean[] received = {false};
        SubjectEventStream stream = new SubjectEventStream();
        IWeatherAlarmService mockAlarmService = TestUtils.getMockAlarmService();
        new AlarmFilterHandler(stream, mockAlarmService);
        stream.observe(WeatherAlarmUpdatedEvent.class).forEach(event -> received[0] = true);
        WeatherAlarm alarm = mockAlarmService.getAlarms().get(0);
        Instant now = Instant.now();
        stream.publish(Observable.just(new NotificationSentEvent(alarm, now)));
        Assert.assertTrue("No event received", received[0]);
        Assert.assertEquals("Last notification time does not match", now, alarm.getLastNotification());
        Assert.assertTrue("Alarm is not triggered", alarm.isTriggered());
    }

    @Test
    public void testHandleFilterNoMatchDueToCriteria() {
        final boolean[] received = {false};
        SubjectEventStream stream = new SubjectEventStream();
        IWeatherAlarmService mockAlarmService = TestUtils.getMockAlarmService();
        new AlarmFilterHandler(stream, TestUtils.getMockAlarmService());
        stream.observe(WeatherAlarmUpdatedEvent.class).forEach(event -> received[0] = true);
        WeatherAlarm alarm = mockAlarmService.getAlarms().get(0);
        alarm.setTriggered(true);
        stream.publish(Observable.just(new FilterNoMatchEvent(FilterNoMatchEvent.Reason.CRITERIA, alarm)));
        Assert.assertTrue("No event received", received[0]);
        Assert.assertEquals("Last notification time does not match", null, alarm.getLastNotification());
        Assert.assertFalse("Alarm is still triggered", alarm.isTriggered());
    }

    @Test
    public void testHandleFilterNoMatchDueToNotReady() {
        final boolean[] received = {false};
        SubjectEventStream stream = new SubjectEventStream();
        IWeatherAlarmService mockAlarmService = TestUtils.getMockAlarmService();
        new AlarmFilterHandler(stream, TestUtils.getMockAlarmService());
        stream.observe(WeatherAlarmUpdatedEvent.class).forEach(event -> received[0] = true);
        WeatherAlarm alarm = mockAlarmService.getAlarms().get(0);
        alarm.setTriggered(true);
        alarm.setLastNotification(Instant.now());
        stream.publish(Observable.just(new FilterNoMatchEvent(FilterNoMatchEvent.Reason.NOT_READY, alarm)));
        Assert.assertFalse("No event received", received[0]);
        Assert.assertTrue("Last notification time is null", alarm.getLastNotification() != null);
        Assert.assertTrue("Alarm is not triggered", alarm.isTriggered());
    }
}
