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

import com.netflix.ribbon.RibbonRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.Observable;
import weatherAlarm.events.IEventStream;
import weatherAlarm.events.SubjectEventStream;
import weatherAlarm.events.WeatherConditionEvent;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.IWeatherAlarmService;
import weatherAlarm.util.TestUtils;

/**
 * This class is responsible for testing {@link weatherAlarm.handlers.WeatherQueryHandler}
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/10/2015
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(WeatherQueryHandler.class)
public class WeatherQueryHandlerTest {
    @Test
    public void testRequestWeatherData() throws Exception {
        final boolean[] received = {false};
        SubjectEventStream stream = new SubjectEventStream();
        createMockHandler(stream);
        stream.observe(WeatherConditionEvent.class)
                .forEach(event -> received[0] = true);
        Thread.sleep(2000L);
        Assert.assertTrue("No event received", received[0]);
    }

    private void createMockHandler(IEventStream stream) throws Exception {
        IWeatherAlarmService alarmService = TestUtils.getMockAlarmService();
        WeatherAlarm alarm = alarmService.getAlarms().get(0);
        WeatherQueryHandler handler = PowerMock.createPartialMock(WeatherQueryHandler.class,
                new String[]{"buildRequest"},
                stream,
                TestUtils.getMockConfigService(IConfigService.CONFIG_WEATHER_SERVICE_QUERY_INTERVAL, "1"),
                alarmService);
        PowerMock.expectPrivate(handler, "buildRequest", alarm.getLocation())
                .andReturn(getMockRibbonRequest())
                .anyTimes();
        PowerMock.replay(handler);
    }

    private RibbonRequest<ByteBuf> getMockRibbonRequest() {
        @SuppressWarnings("unchecked")
        RibbonRequest<ByteBuf> ribbonRequest = EasyMock.createMock(RibbonRequest.class);
        ByteBuf byteBuf = Unpooled.copiedBuffer(TestUtils.getMockJsonResult().getBytes());
        EasyMock.expect(ribbonRequest.observe())
                .andReturn(Observable.just(byteBuf))
                .anyTimes();
        EasyMock.replay(ribbonRequest);
        return ribbonRequest;
    }
}