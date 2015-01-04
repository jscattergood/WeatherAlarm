package weatherAlarm.events;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 1/4/2015
 */
public class EventStream {
    private BehaviorSubject<IModuleEvent> stream = BehaviorSubject.create();

    public void publish(Observable<IModuleEvent> event) {
        event.doOnNext(stream::onNext).subscribe();
    }

    public Observable<IModuleEvent> observe() {
        return stream;
    }

    public Observable<IModuleEvent> observe(Class<? extends IModuleEvent> eventClass) {
        return stream.filter(event -> event.getClass() == eventClass);
    }
}
