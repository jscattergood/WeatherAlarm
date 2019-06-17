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

import rx.Observable;

/**
 * This interface represents a class that provides the ability to publish and observe a stream of events
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/10/2015
 */
public interface IEventStream {
    void publish(Observable<IEvent> event);

    Observable<IEvent> observe();

    <T> Observable<T> observe(Class<T> eventClass);
}
