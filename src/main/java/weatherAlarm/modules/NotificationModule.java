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
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/30/2014
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

    public Func1<? super IModuleEvent, ? extends Observable<IModuleEvent>> sendNotification() {
        return moduleEvent -> Observable.just(moduleEvent);
    }
}
