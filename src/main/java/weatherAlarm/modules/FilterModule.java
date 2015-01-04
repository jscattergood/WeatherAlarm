package weatherAlarm.modules;

import rx.Observable;
import rx.functions.Func1;
import weatherAlarm.events.EventStream;
import weatherAlarm.events.FilterMatchEvent;
import weatherAlarm.events.IModuleEvent;
import weatherAlarm.events.WeatherConditionEvent;

/**
 * This class is responsible for filtering current weather conditions to find ones of interest
 *
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/30/2014
 */
public class FilterModule extends EventModule {
    public FilterModule(EventStream stream) {
        super(stream);
    }

    @Override
    protected void configure() {
        final Observable<IModuleEvent> observableEvent = eventStream
                .observe(WeatherConditionEvent.class)
                .flatMap(filterEvent())
                .map(event -> new FilterMatchEvent());
        eventStream.publish(observableEvent);
    }

    private Func1<? super IModuleEvent, ? extends Observable<IModuleEvent>> filterEvent() {
        return moduleEvent -> Observable.just(moduleEvent);
    }
}
