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

import rx.Observable;
import rx.functions.Func1;
import weatherAlarm.events.FilterMatchEvent;
import weatherAlarm.events.IEvent;
import weatherAlarm.events.IEventStream;

/**
 * This is the base class for all notification handlers. It automatically subscribes to the
 * {@link weatherAlarm.events.IEventStream} for relevant events.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/7/2015
 */
public abstract class AbstractNotificationHandler extends EventHandler {
    public AbstractNotificationHandler(IEventStream stream) {
        super(stream);

        Observable<IEvent> observableEvent = eventStream
                .observe(FilterMatchEvent.class)
                .flatMap(sendNotification());
        eventStream.publish(observableEvent);
    }

    protected abstract Func1<? super FilterMatchEvent, ? extends Observable<IEvent>> sendNotification();
}
