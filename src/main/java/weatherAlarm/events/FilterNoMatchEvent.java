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

/**
 * This event is used to signal that a {@link weatherAlarm.model.WeatherAlarm} did not match the filter criteria
 * and includes the {@link weatherAlarm.events.FilterNoMatchEvent.Reason} that it didn't match.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/4/2015
 */
public class FilterNoMatchEvent implements IEvent {
    private final Reason reason;
    private final WeatherAlarm alarm;

    public FilterNoMatchEvent(Reason reason, WeatherAlarm alarm) {
        this.reason = reason;
        this.alarm = alarm;
    }

    public Reason getReason() {
        return reason;
    }

    public WeatherAlarm getAlarm() {
        return alarm;
    }

    @Override
    public String toString() {
        return "FilterNoMatchEvent[" +
                "reason=" + reason +
                ", alarm=" + alarm +
                ']';
    }

    public enum Reason {
        CRITERIA,
        NOT_READY
    }
}
