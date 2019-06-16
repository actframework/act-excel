package act.view.excel;

/*-
 * #%L
 * ACT Excel
 * %%
 * Copyright (C) 2015 - 2018 ActFramework
 * %%
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
 * #L%
 */

import act.Act;
import act.annotations.Label;
import act.app.ActionContext;
import act.util.LogSupport;
import act.util.PropertySpec;
import act.view.DirectRender;
import org.osgl.$;
import org.osgl.http.H;
import org.osgl.util.*;
import org.osgl.xls.ExcelWriter;

import java.lang.reflect.Field;
import java.util.*;

public class ExcelDirectRender extends LogSupport implements DirectRender {

    public static final ExcelDirectRender INSTANCE = new ExcelDirectRender();

    private volatile ExcelDataFormat.Manager excelDataFormatManager;

    @Override
    public void render(Object result, ActionContext context) {
        MimeType mimeType = MimeType.findByContentType(context.accept().contentType());
        E.illegalStateIfNot(mimeType.test(MimeType.Trait.excel));
        H.Response resp = context.resp();
        resp.contentDisposition(context.attachmentName(), false);
        ExcelWriter.Builder builder = ExcelWriter.builder()
                .dateFormat(context.dateFormatPattern(true))
                .filter(filter(context))
                .headerMap(headerMapping(result, context))
                .fieldStylePatterns(excelDataFormatManager().fieldStyleLookup)
                .headerTransformer(Keyword.Style.READABLE.asTransformer());
        if (mimeType.test(MimeType.Trait.xlsx)) {
            builder.asXlsx();
        }
        builder.build().write(result, context.resp().outputStream());
    }

    private Map<String, String> headerMapping(Object result, ActionContext context) {
        Map<String, String> mapping = new HashMap<>();
        exploreHeaderMapping(result, mapping);
        PropertySpec.MetaInfo spec = context.propertySpec();
        if (null != spec) {
            mapping.putAll(spec.labelMapping(context));
        }
        return mapping;
    }

    private void exploreHeaderMapping(Object result, Map<String, String> mapping) {
        if (result instanceof Map) {
            exploreHeaderMapping((Map) result, mapping);
        } else if (result instanceof List) {
            exploreHeaderMapping((List) result, mapping);
        } else {
            exploreHeaderMappingFromType(result.getClass(), mapping);
        }
    }

    private void exploreHeaderMapping(Map map, Map<String, String> mapping) {
        for (Object o : map.values()) {
            if (o instanceof List) {
                exploreHeaderMapping((List) o, mapping);
            } else {
                exploreHeaderMappingFromType(o.getClass(), mapping);
            }
        }
    }

    private void exploreHeaderMapping(List list, Map<String, String> mapping) {
        for (Object o : list) {
            if (null != o) {
                exploreHeaderMappingFromType(o.getClass(), mapping);
                return;
            }
        }
    }

    private void exploreHeaderMappingFromType(Class type, Map<String, String> mapping) {
        List<Field> fields = $.fieldsOf(type);
        for (Field field : fields) {
            Label label = field.getAnnotation(Label.class);
            if (null != label) {
                mapping.put(field.getName(), label.value());
            }
        }
    }

    private String filter(ActionContext context) {
        PropertySpec.MetaInfo spec = context.propertySpec();
        if (null == spec) {
            return null;
        }
        Set<String> blackList = spec.excludeFieldsForHttp();
        if (!blackList.isEmpty()) {
            S.Buffer buf = S.buffer();
            for (String field : blackList) {
                buf.append("-").append(field).append(",");
            }
            return buf.toString();
        }
        List<String> whiteList = spec.outputFieldsForHttp();
        if (!whiteList.isEmpty()) {
            S.Buffer buf = S.buffer();
            for (String field : whiteList) {
                buf.append(field).append(",");
            }
            return buf.toString();
        }
        return null;
    }

    private ExcelDataFormat.Manager excelDataFormatManager() {
        if (null == excelDataFormatManager) {
            synchronized (this) {
                if (null == excelDataFormatManager) {
                    excelDataFormatManager = Act.getInstance(ExcelDataFormat.Manager.class);
                }
            }
        }
        return excelDataFormatManager;
    }

}
