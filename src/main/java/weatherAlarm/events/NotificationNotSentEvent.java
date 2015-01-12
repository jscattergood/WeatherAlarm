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

package weatherAlarm.events;

import weatherAlarm.model.WeatherAlarm;

/**
 * This event is used to signal that a notification could not be created for a {@link weatherAlarm.model.WeatherAlarm}.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/4/2015
 */
public class NotificationNotSentEvent implements IEvent {
    private final WeatherAlarm alarm;
    private final String reason;

    public NotificationNotSentEvent(WeatherAlarm alarm, String reason) {
        this.alarm = alarm;
        this.reason = reason;
    }

    public WeatherAlarm getAlarm() {
        return alarm;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "NotificationNotSentEvent[" +
                "alarm=" + alarm +
                ", reason='" + reason + '\'' +
                ']';
    }
}
