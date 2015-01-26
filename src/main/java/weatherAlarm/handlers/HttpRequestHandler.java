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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles http requests for service endpoints.
 *
 * @author <a href="https://github.com/jscattergood">John Scattergood</a> 12/28/2014
 */
public class HttpRequestHandler implements RequestHandler<ByteBuf, ByteBuf> {
    private static final String PATH_DELIM = "/";
    private Map<String, RequestHandler<ByteBuf, ByteBuf>> uriHandlers = new HashMap<>();

    public HttpRequestHandler addUriHandler(String uri, RequestHandler<ByteBuf, ByteBuf> requestHandler) {
        this.uriHandlers.put(uri, requestHandler);
        return this;
    }

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        RequestHandler<ByteBuf, ByteBuf> handler = findRequestHandler(request.getUri());
        if (handler != null) {
            return handler.handle(request, response);
        } else {
            response.setStatus(HttpResponseStatus.NOT_FOUND);
            return response.close();
        }
    }

    private RequestHandler<ByteBuf, ByteBuf> findRequestHandler(String uri) {
        for (String uriKey : uriHandlers.keySet()) {
            if (uri.equals(uriKey) || uri.startsWith(uriKey + PATH_DELIM)) {
                return uriHandlers.get(uriKey);
            }
        }
        return null;
    }

    public List<RequestHandler<ByteBuf, ByteBuf>> getUriHandlers() {
        return new ArrayList<>(uriHandlers.values());
    }
}
