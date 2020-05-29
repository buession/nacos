/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.common.http.handler;

import com.alibaba.nacos.common.constant.HttpHeaderConsts;
import com.alibaba.nacos.common.http.client.HttpClientResponse;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.http.param.MediaType;
import com.alibaba.nacos.common.model.RestResult;
import com.alibaba.nacos.common.utils.IoUtils;
import com.alibaba.nacos.common.utils.JacksonUtils;
import org.apache.http.HttpStatus;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Type;
import org.slf4j.Logger;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public final class ResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    public static <T> T convert(String s, Class<T> cls) throws Exception {
        return JacksonUtils.toObj(s, cls);
    }

    public static <T> T convert(String s, Type type) throws Exception {
        return JacksonUtils.toObj(s, type);
    }

    public static <T> T convert(InputStream inputStream, Class<T> tClass) throws Exception{
        return JacksonUtils.toObj(inputStream, tClass);
    }

    @SuppressWarnings({"unchecked", "rawtypes", "resource"})
    public static <T> RestResult<T> responseEntityExtractor(HttpClientResponse response, Type type) throws Exception{
        Header headers = response.getHeaders();
        String contentType = headers.getValue(HttpHeaderConsts.CONTENT_TYPE);
        String body = IoUtils.toString(response.getBody(), headers.getCharset());
        T extractBody = (T) body;
        if (MediaType.APPLICATION_JSON.equals(contentType) && HttpStatus.SC_OK == response.getStatusCode()) {
            extractBody = convert(body, type);
        }
        if (extractBody instanceof RestResult) {
            return (RestResult<T>) extractBody;
        }
        return new RestResult<>(response.getHeaders(), response.getStatusCode(), extractBody);
    }

}
