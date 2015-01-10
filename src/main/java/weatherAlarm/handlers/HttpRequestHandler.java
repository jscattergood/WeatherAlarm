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

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;

/**
 * This class handles http requests for service endpoints.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 12/28/2014
 */
public class HttpRequestHandler implements RequestHandler<ByteBuf, ByteBuf> {

    private final String uri;
    private final RequestHandler<ByteBuf, ByteBuf> uriHandler;

    public HttpRequestHandler(String uri, RequestHandler<ByteBuf, ByteBuf> requestHandler) {
        this.uri = uri;
        this.uriHandler = requestHandler;
    }

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        if (request.getUri().startsWith(uri)) {
            return uriHandler.handle(request, response);
        } else {
            response.setStatus(HttpResponseStatus.NOT_FOUND);
            return response.close();
        }
    }
}
