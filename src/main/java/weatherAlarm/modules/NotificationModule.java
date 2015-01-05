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

import rx.Observable;
import rx.functions.Func1;
import weatherAlarm.events.EventStream;
import weatherAlarm.events.FilterMatchEvent;
import weatherAlarm.events.IModuleEvent;
import weatherAlarm.events.NotificationSentEvent;

/**
 * This class is responsible for performing external notifications
 *
 * @author <a href="mailto:john.scattergood@gmail.com">John Scattergood</a> 12/30/2014
 */
public class NotificationModule extends EventModule {
    public NotificationModule(EventStream stream) {
        super(stream);
    }

    @Override
    protected void configure() {
        Observable<IModuleEvent> observableEvent = eventStream
                .observe(FilterMatchEvent.class)
                .flatMap(sendNotification())
                .map(event -> new NotificationSentEvent());
        eventStream.publish(observableEvent);
    }

    private Func1<? super IModuleEvent, ? extends Observable<IModuleEvent>> sendNotification() {
        return moduleEvent -> Observable.just(moduleEvent);
    }
}
