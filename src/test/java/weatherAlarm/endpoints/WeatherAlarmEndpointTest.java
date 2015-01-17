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

package weatherAlarm.endpoints;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import weatherAlarm.model.WeatherAlarm;
import weatherAlarm.services.IWeatherAlarmService;
import weatherAlarm.util.TestUtils;

import java.net.URLEncoder;
import java.util.Arrays;

public class WeatherAlarmEndpointTest {

    private static final String URI = "/weatherAlarm";

    @Test
    public void testNotImplemented() {
        IWeatherAlarmService alarmService = TestUtils.getMockAlarmService();
        WeatherAlarmEndpoint alarmEndpoint = new WeatherAlarmEndpoint();
        alarmEndpoint.setAlarmService(alarmService);

        Capture<byte[]> written = EasyMock.newCapture();
        Capture<HttpResponseStatus> status = EasyMock.newCapture();
        HttpServerRequest<ByteBuf> request = createMockHttpServerRequest(HttpMethod.POST, URI, Observable.empty());
        HttpServerResponse<ByteBuf> response = createMockHttpResponse(status, written);
        alarmEndpoint.handle(request, response);
        HttpResponseStatus expected = HttpResponseStatus.NOT_IMPLEMENTED;
        Assert.assertEquals("Unexpected value for status", expected, status.getValue());
    }

    @Test
    public void testHandleRequestForAlarms() throws Exception {
        IWeatherAlarmService alarmService = TestUtils.getMockAlarmService();
        WeatherAlarmEndpoint alarmEndpoint = new WeatherAlarmEndpoint();
        alarmEndpoint.setAlarmService(alarmService);

        Capture<byte[]> written = EasyMock.newCapture();
        Capture<HttpResponseStatus> status = EasyMock.newCapture();
        HttpServerRequest<ByteBuf> request = createMockHttpServerRequest(HttpMethod.GET, URI, Observable.empty());
        HttpServerResponse<ByteBuf> response = createMockHttpResponse(status, written);
        alarmEndpoint.handle(request, response);
        byte[] expected = new ObjectMapper().writeValueAsBytes(alarmService.getAlarms());
        Assert.assertTrue("Unexpected value written", Arrays.equals(expected, written.getValue()));
    }

    @Test
    public void testHandleRequestForAlarm() throws Exception {
        IWeatherAlarmService alarmService = TestUtils.getMockAlarmService();
        WeatherAlarmEndpoint alarmEndpoint = new WeatherAlarmEndpoint();
        alarmEndpoint.setAlarmService(alarmService);
        WeatherAlarm alarm = alarmService.getAlarms().get(0);

        Capture<byte[]> written = EasyMock.newCapture();
        Capture<HttpResponseStatus> status = EasyMock.newCapture();
        String uri = URI + "/" + URLEncoder.encode(alarm.getName(), "UTF-8");
        HttpServerRequest<ByteBuf> request = createMockHttpServerRequest(HttpMethod.GET, uri, Observable.empty());
        HttpServerResponse<ByteBuf> response = createMockHttpResponse(status, written);
        alarmEndpoint.handle(request, response);
        byte[] expected = new ObjectMapper().writeValueAsBytes(alarm);
        Assert.assertTrue("Unexpected value written", Arrays.equals(expected, written.getValue()));
    }

    @Test
    public void testHandleRequestForAlarmNotFound() throws Exception {
        IWeatherAlarmService alarmService = TestUtils.getMockAlarmService();
        WeatherAlarmEndpoint alarmEndpoint = new WeatherAlarmEndpoint();
        alarmEndpoint.setAlarmService(alarmService);
        WeatherAlarm alarm = alarmService.getAlarms().get(0);

        Capture<byte[]> written = EasyMock.newCapture();
        Capture<HttpResponseStatus> status = EasyMock.newCapture();
        String uri = URI + "/unknownAlarm";
        HttpServerRequest<ByteBuf> request = createMockHttpServerRequest(HttpMethod.GET, uri, Observable.empty());
        HttpServerResponse<ByteBuf> response = createMockHttpResponse(status, written);
        alarmEndpoint.handle(request, response);
        Assert.assertEquals("Unexpected status", HttpResponseStatus.NOT_FOUND, status.getValue());
    }

    @Test
    public void testHandleRequestForAddAlarm() throws Exception {
        IWeatherAlarmService alarmService = TestUtils.getEmptyAlarmService();
        WeatherAlarmEndpoint alarmEndpoint = new WeatherAlarmEndpoint();
        alarmEndpoint.setAlarmService(alarmService);

        WeatherAlarm alarm = TestUtils.createWeatherAlarm();

        Capture<byte[]> written = EasyMock.newCapture();
        Capture<HttpResponseStatus> status = EasyMock.newCapture();
        HttpServerRequest<ByteBuf> request = createMockHttpServerRequest(HttpMethod.PUT, URI,
                createContent(new ObjectMapper().writeValueAsBytes(alarm)));
        HttpServerResponse<ByteBuf> response = createMockHttpResponse(status, written);
        alarmEndpoint.handle(request, response);
        Assert.assertTrue("Alarm not added from list " + alarm, alarmService.getAlarm(alarm.getName()) != null);
    }

    @Test
    public void testHandleRequestForDeleteAlarm() throws Exception {
        IWeatherAlarmService alarmService = TestUtils.getMockAlarmService();
        WeatherAlarmEndpoint alarmEndpoint = new WeatherAlarmEndpoint();
        alarmEndpoint.setAlarmService(alarmService);
        WeatherAlarm alarm = alarmService.getAlarms().get(0);

        Capture<byte[]> written = EasyMock.newCapture();
        Capture<HttpResponseStatus> status = EasyMock.newCapture();
        String encodedAlarmName = URLEncoder.encode(alarm.getName(), "UTF-8");
        String uri = URI + "/" + encodedAlarmName;
        HttpServerRequest<ByteBuf> request = createMockHttpServerRequest(HttpMethod.DELETE, uri, Observable.empty());
        HttpServerResponse<ByteBuf> response = createMockHttpResponse(status, written);
        alarmEndpoint.handle(request, response);
        Assert.assertTrue("Alarm not deleted from list " + alarm, !alarmService.getAlarms().contains(alarm));
    }

    @Test
    public void testHandleRequestForDeleteAlarms() throws Exception {
        IWeatherAlarmService alarmService = TestUtils.getMockAlarmService();
        WeatherAlarmEndpoint alarmEndpoint = new WeatherAlarmEndpoint();
        alarmEndpoint.setAlarmService(alarmService);
        WeatherAlarm alarm = alarmService.getAlarms().get(0);

        Capture<byte[]> written = EasyMock.newCapture();
        Capture<HttpResponseStatus> status = EasyMock.newCapture();
        HttpServerRequest<ByteBuf> request = createMockHttpServerRequest(HttpMethod.DELETE, URI, Observable.empty());
        HttpServerResponse<ByteBuf> response = createMockHttpResponse(status, written);
        alarmEndpoint.handle(request, response);
        Assert.assertTrue("Alarm deleted from list " + alarm, alarmService.getAlarms().contains(alarm));
        Assert.assertEquals("Unexpected status", HttpResponseStatus.UNAUTHORIZED, status.getValue());
    }

    private Observable<ByteBuf> createContent(byte[] bytes) {
        return Observable.just(Unpooled.copiedBuffer(bytes));
    }

    @SuppressWarnings("unchecked")
    private HttpServerRequest<ByteBuf> createMockHttpServerRequest(HttpMethod method,
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
    private HttpServerResponse<ByteBuf> createMockHttpResponse(Capture<HttpResponseStatus> captureResponseStatus,
                                                               Capture<byte[]> captureWrittenBytes) {
        HttpServerResponse<ByteBuf> mockResponse = EasyMock.createMock(HttpServerResponse.class);
        mockResponse.writeBytes(EasyMock.capture(captureWrittenBytes));
        mockResponse.setStatus(EasyMock.capture(captureResponseStatus));
        EasyMock.expect(mockResponse.close()).andReturn(Observable.empty()).anyTimes();
        EasyMock.replay(mockResponse);
        return mockResponse;
    }
}