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

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;

import static weatherAlarm.util.TestUtils.createMockHttpResponse;
import static weatherAlarm.util.TestUtils.createMockHttpServerRequest;

public class HttpRequestHandlerTest {

    @Test
    public void testHandleSuccess() throws Exception {
        String uri = "/test";
        HttpRequestHandler requestHandler = new HttpRequestHandler()
                .addUriHandler(uri, (request, response) -> {
                    response.setStatus(HttpResponseStatus.OK);
                    return response.close();
                });

        Capture<byte[]> written = EasyMock.newCapture();
        Capture<HttpResponseStatus> status = EasyMock.newCapture();
        HttpServerRequest<ByteBuf> request = createMockHttpServerRequest(HttpMethod.GET, uri, Observable.empty());
        HttpServerResponse<ByteBuf> response = createMockHttpResponse(status, written);
        requestHandler.handle(request, response);
        HttpResponseStatus expected = HttpResponseStatus.OK;
        Assert.assertEquals("Unexpected value for status", expected, status.getValue());
    }

    @Test
    public void testHandleNotFound() throws Exception {
        String uri = "/test";
        HttpRequestHandler requestHandler = new HttpRequestHandler()
                .addUriHandler(uri, (request, response) -> {
                    response.setStatus(HttpResponseStatus.OK);
                    return response.close();
                });

        Capture<byte[]> written = EasyMock.newCapture();
        Capture<HttpResponseStatus> status = EasyMock.newCapture();
        HttpServerRequest<ByteBuf> request = createMockHttpServerRequest(HttpMethod.GET,
                uri + "again", Observable.empty());
        HttpServerResponse<ByteBuf> response = createMockHttpResponse(status, written);
        requestHandler.handle(request, response);
        HttpResponseStatus expected = HttpResponseStatus.NOT_FOUND;
        Assert.assertEquals("Unexpected value for status", expected, status.getValue());
    }
}
