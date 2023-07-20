package com.q.reminder.reminder.handler;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    private ServletInputStream inputStream;

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
            body = StreamUtils.copyToByteArray(request.getInputStream());
            inputStream = new RequestCachingInputStream(body);
        }
    }

    public String getBody() {
        return new String(body);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (inputStream != null) {
            return inputStream;
        }
        return super.getInputStream();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return super.getParameterMap();
    }

    private static class RequestCachingInputStream extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        public RequestCachingInputStream(byte[] bytes) {
            inputStream = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readlistener) {
        }

    }
}
