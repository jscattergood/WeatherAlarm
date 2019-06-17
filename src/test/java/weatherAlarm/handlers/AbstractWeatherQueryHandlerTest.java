/*
 * Copyright 2019 John Scattergood
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
import org.powermock.api.easymock.PowerMock;
import rx.Observable;
import weatherAlarm.events.IEventStream;
import weatherAlarm.events.SubjectEventStream;
import weatherAlarm.events.WeatherConditionEvent;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.IWeatherAlarmService;
import weatherAlarm.util.TestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for weather query handler tests
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 3/28/2015
 */
public abstract class AbstractWeatherQueryHandlerTest {
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

    protected abstract Class<? extends AbstractWeatherQueryHandler> getHandlerClass();

    protected abstract String getMockJsonResult();

    private void createMockHandler(IEventStream stream) throws Exception {
        IWeatherAlarmService alarmService = TestUtils.getMockAlarmService();
        WeatherAlarm alarm = alarmService.getAlarms().get(0);
        Map<String, String> configMap = new HashMap<>();
        configMap.put(IConfigService.CONFIG_WEATHER_SERVICE_QUERY_INTERVAL, "1");
        configMap.put(IConfigService.CONFIG_WEATHER_SERVICE_API_KEY, "1234567");
        IConfigService mockConfigService = TestUtils.getMockConfigService(configMap);
        AbstractWeatherQueryHandler handler = PowerMock.createPartialMock(getHandlerClass(),
                new String[]{"buildRequest"},
                stream,
                mockConfigService,
                alarmService);
        PowerMock.expectPrivate(handler, "buildRequest", alarm.getLocation())
                .andReturn(getMockRibbonRequest())
                .anyTimes();
        PowerMock.replay(handler);
    }

    private RibbonRequest<ByteBuf> getMockRibbonRequest() {
        @SuppressWarnings("unchecked")
        RibbonRequest<ByteBuf> ribbonRequest = EasyMock.createMock(RibbonRequest.class);
        ByteBuf byteBuf = Unpooled.copiedBuffer(getMockJsonResult().getBytes());
        EasyMock.expect(ribbonRequest.observe())
                .andReturn(Observable.just(byteBuf))
                .anyTimes();
        EasyMock.replay(ribbonRequest);
        return ribbonRequest;
    }
}
