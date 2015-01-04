package weatherAlarm.modules;

import rx.Observable;
import rx.functions.Func1;
import weatherAlarm.events.IModuleEvent;

/**
 * This class is responsible for filtering current weather conditions to find ones of interest
 *
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/30/2014
 */
public class FilterModule extends BaseModule {
    @Override
    protected void configure() {

    }

    public Func1<? super IModuleEvent, ? extends Observable<IModuleEvent>> filterEvent() {
        return moduleEvent -> Observable.just(moduleEvent);
    }
}
