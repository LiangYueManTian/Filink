package com.fiberhome.filink.filinkoceanconnectserver.utils;

import org.apache.http.*;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.Locale;

/**
 * 可关闭响应
 * @author CongcaiYu
 */
@SuppressWarnings("deprecation")
public class StreamClosedHttpResponse implements HttpResponse {
    private final HttpResponse original;

    private final String content;

    /**
     * 构造方法
     * @param original 响应
     * @throws UnsupportedOperationException 不支持操作异常
     * @throws IOException IO异常
     */
    public StreamClosedHttpResponse(final HttpResponse original)
            throws UnsupportedOperationException, IOException {
        this.original = original;

        HttpEntity entity = original.getEntity();
        if (entity != null && entity.isStreaming()) {
            String encoding = entity.getContentEncoding() != null
                    ? entity.getContentEncoding().getValue() : null;
            content = StreamUtil.inputStream2String(entity.getContent(),
                    encoding);
        } else {
            content = null;
        }
    }

    /**
     * 获取状态
     * @return 状态
     */
    @Override
    public StatusLine getStatusLine() {
        return original.getStatusLine();
    }

    /**
     * 设置状态
     * @param statusline 状态
     */
    @Override
    public void setStatusLine(final StatusLine statusline) {
        original.setStatusLine(statusline);
    }

    /**
     * 设置状态
     * @param ver 协议版本
     * @param code code码
     */
    @Override
    public void setStatusLine(final ProtocolVersion ver, final int code) {
        original.setStatusLine(ver, code);
    }

    /**
     * 设置状态
     * @param ver 协议版本
     * @param code code码
     * @param reason 原因
     */
    @Override
    public void setStatusLine(final ProtocolVersion ver, final int code,
                              final String reason) {
        original.setStatusLine(ver, code, reason);
    }

    /**
     * 设置状态码
     * @param code 状态码
     * @throws IllegalStateException 异常
     */
    @Override
    public void setStatusCode(final int code) throws IllegalStateException {
        original.setStatusCode(code);
    }

    /**
     * 设置ReasonPhrase
     * @param reason 原因
     * @throws IllegalStateException 异常
     */
    @Override
    public void setReasonPhrase(final String reason)
            throws IllegalStateException {
        original.setReasonPhrase(reason);
    }

    /**
     * 获取实体
     * @return http对象
     */
    @Override
    public HttpEntity getEntity() {
        return original.getEntity();
    }

    /**
     * 设置实体
     * @param entity http对象
     */
    @Override
    public void setEntity(final HttpEntity entity) {
        original.setEntity(entity);
    }

    /**
     * 获取当前环境
     * @return 当前环境
     */
    @Override
    public Locale getLocale() {
        return original.getLocale();
    }

    /**
     * 设置当前环境
     * @param loc 当前环境
     */
    @Override
    public void setLocale(final Locale loc) {
        original.setLocale(loc);
    }

    /**
     * 获取协议版本
     * @return 协议版本
     */
    @Override
    public ProtocolVersion getProtocolVersion() {
        return original.getProtocolVersion();
    }

    /**
     * 是否包含头
     * @param name 头名称
     * @return 是否包含
     */
    @Override
    public boolean containsHeader(final String name) {
        return original.containsHeader(name);
    }

    /**
     * 获取头信息
     * @param name 头名称
     * @return 头信息
     */
    @Override
    public Header[] getHeaders(final String name) {
        return original.getHeaders(name);
    }

    /**
     * 获取第一个头信息
     * @param name 头名称
     * @return 头对象信息
     */
    @Override
    public Header getFirstHeader(final String name) {
        return original.getFirstHeader(name);
    }

    /**
     * 获取最后一个头信息
     * @param name 头名称
     * @return 头信息
     */
    @Override
    public Header getLastHeader(final String name) {
        return original.getLastHeader(name);
    }

    /**
     * 获取所有头信息
     * @return 头信息数组
     */
    @Override
    public Header[] getAllHeaders() {
        return original.getAllHeaders();
    }

    /**
     * 新增头信息
     * @param header 头对象信息
     */
    @Override
    public void addHeader(final Header header) {
        original.addHeader(header);
    }

    /**
     * 新增头信息
     * @param name 头名称
     * @param value 头值
     */
    @Override
    public void addHeader(final String name, final String value) {
        original.addHeader(name, value);
    }

    /**
     * 设置头信息
     * @param header 头对象
     */
    @Override
    public void setHeader(final Header header) {
        original.setHeader(header);
    }

    /**
     * 设置头信息
     * @param name 头名称
     * @param value 值
     */
    @Override
    public void setHeader(final String name, final String value) {
        original.setHeader(name, value);
    }

    /**
     * 批量设置头信息
     * @param headers 头对象数组
     */
    @Override
    public void setHeaders(final Header[] headers) {
        original.setHeaders(headers);
    }

    /**
     * 删除头信息
     * @param header 头对象
     */
    @Override
    public void removeHeader(final Header header) {
        original.removeHeader(header);
    }

    /**
     * 批量删除头信息
     * @param name 头名称
     */
    @Override
    public void removeHeaders(final String name) {
        original.removeHeaders(name);
    }

    /**
     * 头迭代
     * @return 迭代器
     */
    @Override
    public HeaderIterator headerIterator() {
        return original.headerIterator();
    }

    /**
     * 头迭代器
     * @param name 头名称
     * @return 头迭代
     */
    @Override
    public HeaderIterator headerIterator(final String name) {
        return original.headerIterator(name);
    }

    /**
     * toString方法
     * @return string
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpResponseProxy{");
        sb.append(original);
        sb.append('}');
        return sb.toString();
    }

    /**
     * 获取参数信息
     * @return 请求参数
     */
    @Override
    @Deprecated
    public HttpParams getParams() {
        return original.getParams();
    }

    /**
     * 设置参数信息
     * @param params 参数信息
     */
    @Override
    @Deprecated
    public void setParams(final HttpParams params) {
        original.setParams(params);
    }

    /**
     * 获取body信息
     * @return body
     */
    public String getContent() {
        return content;
    }
}
