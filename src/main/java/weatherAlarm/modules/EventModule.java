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

import com.google.inject.AbstractModule;
import com.netflix.governator.guice.LifecycleInjectorBuilderSuite;
import weatherAlarm.events.EventStream;

/**
 * @author <a href="mailto:john.scattergood@gmail.com">John Scattergood</a> 1/3/2015
 */
public abstract class EventModule extends AbstractModule {

    protected EventStream eventStream;

    public EventModule(EventStream stream) {
        eventStream = stream;
    }

    public LifecycleInjectorBuilderSuite asSuite() {
        final AbstractModule finalThis = this;
        return builder -> builder.withAdditionalModules(finalThis);
    }
}
