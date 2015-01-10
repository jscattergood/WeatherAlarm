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

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * @author <a href="mailto:john.scattergood@gmail.com">John Scattergood</a> 1/4/2015
 */
public class PublishEventStream implements IEventStream {
    private BehaviorSubject<IModuleEvent> stream = BehaviorSubject.create();

    @Override
    public void publish(Observable<IModuleEvent> event) {
        event.doOnNext(stream::onNext).subscribe();
    }

    @Override
    public Observable<IModuleEvent> observe() {
        return stream;
    }

    @Override
    public <T> Observable<T> observe(Class<T> eventClass) {
        return stream.ofType(eventClass);
    }
}
