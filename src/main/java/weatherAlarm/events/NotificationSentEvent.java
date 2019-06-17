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

package weatherAlarm.events;

import weatherAlarm.model.WeatherAlarm;

import java.time.Instant;

/**
 * This event is used to signal that a notification was sent for a {@link weatherAlarm.model.WeatherAlarm}.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/4/2015
 */
public class NotificationSentEvent implements IEvent {
    private final WeatherAlarm alarm;
    private final Instant eventTime;

    public NotificationSentEvent(WeatherAlarm alarm, Instant eventTime) {
        this.alarm = alarm;
        this.eventTime = eventTime;
    }

    public WeatherAlarm getAlarm() {
        return alarm;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    @Override
    public String toString() {
        return "NotificationSentEvent[" +
                "alarm=" + alarm +
                ", eventTime=" + eventTime +
                ']';
    }
}
