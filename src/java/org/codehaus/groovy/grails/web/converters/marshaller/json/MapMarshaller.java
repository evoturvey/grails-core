/*
 * Copyright 2004-2008 the original author or authors.
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
package org.codehaus.groovy.grails.web.converters.marshaller.json;

import grails.converters.JSON;
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException;
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller;
import org.codehaus.groovy.grails.web.json.JSONWriter;

import java.util.Map;

/**
 * @author Siegfried Puchbauer
 * @since 1.1
 */
public class MapMarshaller implements ObjectMarshaller<JSON> {

    public boolean supports(Object object) {
        return object instanceof Map;
    }

    @SuppressWarnings("unchecked")
    public void marshalObject(Object o, JSON converter) throws ConverterException {
        JSONWriter writer = converter.getWriter();
        writer.object();
        Map<Object,Object> map = (Map<Object,Object>) o;
        for (Map.Entry<Object,Object> entry : map.entrySet()) {
            Object key = entry.getKey();
            if(key != null) {
            	writer.key(key.toString());
                converter.convertAnother(entry.getValue());
            }
        }
        writer.endObject();
    }
}
