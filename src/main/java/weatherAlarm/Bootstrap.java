package weatherAlarm;

import netflix.karyon.Karyon;
import netflix.karyon.KaryonBootstrapSuite;
import netflix.karyon.servo.KaryonServoModule;
import netflix.karyon.transport.http.health.HealthCheckEndpoint;
import weatherAlarm.endpoints.HealthCheck;
import weatherAlarm.events.EventStream;
import weatherAlarm.handlers.HttpRequestHandler;
import weatherAlarm.modules.FilterModule;
import weatherAlarm.modules.NotificationModule;
import weatherAlarm.modules.WeatherQueryModule;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/27/2014
 */
public class Bootstrap {

    public static void main(String[] args) {
        HealthCheck healthCheckHandler = new HealthCheck();

        EventStream events = new EventStream();
        WeatherQueryModule weatherQueryModule = new WeatherQueryModule(events);
        FilterModule filterModule = new FilterModule(events);
        NotificationModule notificationModule = new NotificationModule(events);

        events.observe().doOnNext(System.out::println).subscribe();

        Karyon.forRequestHandler(8888,
                new HttpRequestHandler("/health",
                        new HealthCheckEndpoint(healthCheckHandler)),
                new KaryonBootstrapSuite(healthCheckHandler),
                weatherQueryModule.asSuite(),
                filterModule.asSuite(),
                notificationModule.asSuite(),
                KaryonServoModule.asSuite())
                .startAndWaitTillShutdown();
    }
}
