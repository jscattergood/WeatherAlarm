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

package weatherAlarm.handlers;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * This class is responsible for testing {@link YahooWeatherQueryHandler}
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/10/2015
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(YahooWeatherQueryHandler.class)
public class YahooWeatherQueryHandlerTest extends AbstractWeatherQueryHandlerTest {
    @Override
    protected Class<? extends AbstractWeatherQueryHandler> getHandlerClass() {
        return YahooWeatherQueryHandler.class;
    }

    @Override
    protected String getMockJsonResult() {
        return "{\"query\":{" +
                "\"count\":1," +
                "\"created\":\"2015-01-12T01:12:22Z\"," +
                "\"lang\":\"en-US\"," +
                "\"results\":{" +
                "\"channel\":{" +
                "\"item\":{" +
                "\"condition\":{" +
                "\"code\":\"34\"," +
                "\"date\":\"Sun, 11 Jan 2015 4:52 pm PST\"," +
                "\"temp\":\"56\"," +
                "\"text\":\"Fair\"}}}}}}";
    }
}