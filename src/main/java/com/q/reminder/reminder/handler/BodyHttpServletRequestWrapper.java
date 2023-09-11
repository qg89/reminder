package com.q.reminder.reminder.handler;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handler.BodyHttpServletRequestWrapper
 * @Description :
 * @date :  2023/7/20 16:29
 */
public class BodyHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private byte[] body;
    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request the {@link HttpServletRequest} to be wrapped.
     * @throws IllegalArgumentException if the request is null
     */
    public BodyHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        StandardServletMultipartResolver standardServletMultipartResolver = new StandardServletMultipartResolver();
        //做判断，过滤掉form表单形式的，避免form表单的参数
        if (standardServletMultipartResolver.isMultipart(request)) {

        } else {
            body = IOUtils.toByteArray(super.getInputStream());
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new RequestCachingInputStream(body);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public String getBody() {
        return new String(body);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return super.getParameterMap();
    }

    private static class RequestCachingInputStream extends ServletInputStream {

        private byte[] body;
        private int lastIndexRetrieved = -1;
        private ReadListener listener;

        public RequestCachingInputStream(byte[] body) {
            this.body = body;
        }

        @Override
        public int read() throws IOException {
            if (isFinished()) {
                return -1;
            }
            int i = body[lastIndexRetrieved + 1];
            lastIndexRetrieved++;
            if (isFinished() && listener != null) {
                try {
                    listener.onAllDataRead();
                } catch (IOException e) {
                    listener.onError(e);
                    throw e;
                }
            }
            return i;
        }

        @Override
        public boolean isFinished() {
            return lastIndexRetrieved == body.length - 1;
        }

        @Override
        public boolean isReady() {
            return isFinished();
        }

        @Override
        public void setReadListener(ReadListener readlistener) {
            if (listener == null) {
                throw new IllegalArgumentException("listener cann not be null");
            }
            if (this.listener != null) {
                throw new IllegalArgumentException("listener has been set");
            }
            this.listener = listener;
            if (!isFinished()) {
                try {
                    listener.onAllDataRead();
                } catch (IOException e) {
                    listener.onError(e);
                }
            } else {
                try {
                    listener.onAllDataRead();
                } catch (IOException e) {
                    listener.onError(e);
                }
            }
        }
        @Override
        public int available() throws IOException {
            return body.length - lastIndexRetrieved - 1;
        }

        @Override
        public void close() throws IOException {
            lastIndexRetrieved = body.length - 1;
            body = null;
        }
    }
}
