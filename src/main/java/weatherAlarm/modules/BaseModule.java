package weatherAlarm.modules;

import com.google.inject.AbstractModule;
import com.netflix.governator.guice.LifecycleInjectorBuilderSuite;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import weatherAlarm.events.IModuleEvent;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 1/3/2015
 */
public abstract class BaseModule extends AbstractModule {

    protected BehaviorSubject<IModuleEvent> subject = BehaviorSubject.create();

    public LifecycleInjectorBuilderSuite asSuite() {
        final AbstractModule finalThis = this;
        return builder -> builder.withAdditionalModules(finalThis);
    }

    public Observable<IModuleEvent> observe() {
        return subject;
    }
}
