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

package weatherAlarm;

import netflix.karyon.Karyon;
import netflix.karyon.KaryonBootstrapSuite;
import netflix.karyon.servo.KaryonServoModule;
import netflix.karyon.transport.http.health.HealthCheckEndpoint;
import weatherAlarm.endpoints.HealthCheck;
import weatherAlarm.events.EventStream;
import weatherAlarm.handlers.HttpRequestHandler;
import weatherAlarm.modules.AlarmFilterModule;
import weatherAlarm.modules.EmailNotificationModule;
import weatherAlarm.modules.WeatherQueryModule;

/**
 * @author <a href="mailto:john.scattergood@gmail.com">John Scattergood</a> 12/27/2014
 */
public class Bootstrap {

    public static void main(String[] args) {
        HealthCheck healthCheckHandler = new HealthCheck();

        EventStream events = new EventStream();
        WeatherQueryModule weatherQueryModule = new WeatherQueryModule(events);
        AlarmFilterModule alarmFilterModule = new AlarmFilterModule(events);
        EmailNotificationModule notificationModule = new EmailNotificationModule(events);

        events.observe().doOnNext(System.out::println).subscribe();

        Karyon.forRequestHandler(8888,
                new HttpRequestHandler("/health",
                        new HealthCheckEndpoint(healthCheckHandler)),
                new KaryonBootstrapSuite(healthCheckHandler),
                weatherQueryModule.asSuite(),
                alarmFilterModule.asSuite(),
                notificationModule.asSuite(),
                KaryonServoModule.asSuite())
                .startAndWaitTillShutdown();
    }
}
