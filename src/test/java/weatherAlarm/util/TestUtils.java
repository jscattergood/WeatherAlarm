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

package weatherAlarm.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import org.easymock.Capture;
import org.easymock.EasyMock;
import rx.Observable;
import weatherAlarm.model.IntegerPredicate;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.model.WeatherDataEnum;
import weatherAlarm.services.IConfigService;
import weatherAlarm.services.IWeatherAlarmService;
import weatherAlarm.services.SimpleAlarmService;

import java.util.Map;

/**
 * Utility class for reusable test scaffolding
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 1/11/2015
 */
public class TestUtils {
    private TestUtils() {

    }

    public static IWeatherAlarmService getMockAlarmService() {
        return getMockAlarmService(1);
    }

    public static IWeatherAlarmService getMockAlarmService(int numberOfAlarms) {
        IWeatherAlarmService alarmService = new SimpleAlarmService(getMockConfigService());
        for (int i = 0; i < numberOfAlarms; i++) {
            WeatherAlarm alarm = createWeatherAlarm(i);
            alarmService.addAlarm(alarm);
        }
        return alarmService;
    }

    public static IWeatherAlarmService getEmptyAlarmService() {
        return new SimpleAlarmService(getMockConfigService());
    }

    public static WeatherAlarm createWeatherAlarm() {
        return createWeatherAlarm(0);
    }

    public static WeatherAlarm createWeatherAlarm(int suffix) {
        WeatherAlarm alarm = new WeatherAlarm("zero degrees " + suffix);
        alarm.setEmailAddress("joe@xyz.com");
        IntegerPredicate valuePredicate = new IntegerPredicate(PredicateEnum.GT, 0);
        alarm.setCriteriaFor(WeatherDataEnum.TEMPERATURE, valuePredicate);
        alarm.setLocation("99999");
        return alarm;
    }

    public static IConfigService getMockConfigService() {
        IConfigService configService = EasyMock.createNiceMock(IConfigService.class);
        EasyMock.replay(configService);
        return configService;
    }

    public static IConfigService getMockConfigService(Map<String, String> inConfigs) {
        IConfigService configService = EasyMock.createNiceMock(IConfigService.class);
        for (Map.Entry<String, String> entry : inConfigs.entrySet()) {
            EasyMock.expect(configService.getConfigValue(entry.getKey())).andReturn(entry.getValue()).anyTimes();
        }
        EasyMock.replay(configService);
        return configService;
    }

    @SuppressWarnings("unchecked")
    public static HttpServerRequest<ByteBuf> createMockHttpServerRequest(HttpMethod method,
                                                                         String uri,
                                                                         Observable<ByteBuf> content) {
        HttpServerRequest<ByteBuf> request = EasyMock.createMock(HttpServerRequest.class);
        EasyMock.expect(request.getUri()).andReturn(uri).anyTimes();
        EasyMock.expect(request.getHttpMethod()).andReturn(method).anyTimes();
        EasyMock.expect(request.getContent()).andReturn(content).anyTimes();
        EasyMock.replay(request);
        return request;
    }

    @SuppressWarnings("unchecked")
    public static HttpServerResponse<ByteBuf> createMockHttpResponse(Capture<HttpResponseStatus> captureResponseStatus,
                                                                     Capture<byte[]> captureWrittenBytes) {
        HttpServerResponse<ByteBuf> mockResponse = EasyMock.createMock(HttpServerResponse.class);
        mockResponse.writeBytes(EasyMock.capture(captureWrittenBytes));
        mockResponse.setStatus(EasyMock.capture(captureResponseStatus));
        EasyMock.expect(mockResponse.close()).andReturn(Observable.empty()).anyTimes();
        EasyMock.replay(mockResponse);
        return mockResponse;
    }
}
