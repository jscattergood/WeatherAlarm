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
import weatherAlarm.endpoints.WeatherAlarmEndpoint;
import weatherAlarm.handlers.HttpRequestHandler;

/**
 * This class contains the default main method for the application.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 12/27/2014
 */
public class Bootstrap {

    public static void main(String[] args) {
        HealthCheck healthCheckHandler = new HealthCheck();
        WeatherAlarmEndpoint alarmEndpoint = new WeatherAlarmEndpoint();
        HttpRequestHandler requestHandler = new HttpRequestHandler()
                .addUriHandler("/healthcheck", new HealthCheckEndpoint(healthCheckHandler))
                .addUriHandler("/weatherAlarm", alarmEndpoint);

        WeatherAlarmModule module = new WeatherAlarmModule(requestHandler.getUriHandlers());
        Karyon.forRequestHandler(8888,
                requestHandler,
                new KaryonBootstrapSuite(healthCheckHandler),
                module.asSuite(),
                KaryonServoModule.asSuite())
                .startAndWaitTillShutdown();
    }
}
