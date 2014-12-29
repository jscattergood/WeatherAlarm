package weatherAlarm.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import rx.Observable;

/**
 * @author <a href="mailto:john.scattergood@navis.com">John Scattergood</a> 12/28/2014
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
