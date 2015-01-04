package weatherAlarm.modules;

import com.google.inject.AbstractModule;
import com.netflix.governator.guice.LifecycleInjectorBuilderSuite;
import weatherAlarm.events.EventStream;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 1/3/2015
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
