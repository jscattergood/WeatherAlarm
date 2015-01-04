package weatherAlarm.modules;

import rx.Observable;
import rx.functions.Func1;
import weatherAlarm.events.IModuleEvent;

/**
 * This class is responsible for performing external notifications
 *
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/30/2014
 */
public class NotificationModule extends BaseModule {
    @Override
    protected void configure() {

    }

    public Func1<? super IModuleEvent, ? extends Observable<IModuleEvent>> sendNotification() {
        return moduleEvent -> Observable.just(moduleEvent);
    }
}
