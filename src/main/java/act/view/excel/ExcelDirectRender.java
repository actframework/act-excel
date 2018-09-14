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
import act.app.ActionContext;
import act.util.PropertySpec;
import act.view.DirectRender;
import org.osgl.http.H;
import org.osgl.util.*;
import org.osgl.xls.ExcelWriter;

import java.util.*;

public class ExcelDirectRender implements DirectRender {

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
                .headerMap(headerMapping(context))
                .fieldStylePatterns(excelDataFormatManager().fieldStyleLookup)
                .headerTransformer(Keyword.Style.READABLE.asTransformer());
        if (mimeType.test(MimeType.Trait.xlsx)) {
            builder.asXlsx();
        }
        builder.build().write(result, context.resp().outputStream());
    }

    private Map<String, String> headerMapping(ActionContext context) {
        PropertySpec.MetaInfo spec = context.propertySpec();
        return null == spec ? C.<String, String>Map() : spec.labelMapping();
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
