package act.view.excel;

/*-
 * #%L
 * ACT Excel
 * %%
 * Copyright (C) 2015 - 2017 ActFramework
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
import act.view.TemplateBase;
import org.apache.commons.jexl3.JexlBuilder;
import org.jxls.common.Context;
import org.jxls.expression.JexlExpressionEvaluator;
import org.jxls.transform.Transformer;
import org.jxls.util.JxlsHelper;
import org.jxls.util.TransformerFactory;
import org.osgl.$;
import org.osgl.http.H;
import org.osgl.mvc.result.InternalServerError;
import org.osgl.util.E;
import org.osgl.util.IO;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

class ExcelTemplate extends TemplateBase {

    private URL resource;

    ExcelTemplate(URL url) {
        this.resource = $.requireNotNull(url);
    }

    /**
     * Setup the content-type and content-disposition
     */
    @Override
    protected void beforeRender(ActionContext context) {
        H.Format format = context.accept();
        String contentType = format.contentType();
        H.Response response = context.resp();
        response.contentType(contentType);

        if(!response.containsHeader("content-disposition")) {
            response.contentDisposition(context.attachmentName(), false);
        }
    }

    @Override
    protected void merge(Map<String, Object> renderArgs, H.Response response) {
        Context context = new Context(renderArgs);
        InputStream is = IO.inputStream(resource);
        try {
            Transformer transformer = TransformerFactory.createTransformer(is, response.outputStream());
            JexlExpressionEvaluator evaluator = (JexlExpressionEvaluator)transformer.getTransformationConfig().getExpressionEvaluator();
            JexlFunctionManager funcMgr = Act.getInstance(JexlFunctionManager.class);
            evaluator.setJexlEngine(new JexlBuilder().namespaces(funcMgr.functions()).create());
            // obsolete: evaluator.getJexlEngine().setFunctions(funcMgr.functions());
            JxlsHelper.getInstance().processTemplate(context, transformer);
        } catch (IOException e) {
            throw new InternalServerError(e, "Error processing excel template: %s", resource.getPath());
        }
    }

    @Override
    protected String render(Map<String, Object> map) {
        throw E.unsupport();
    }
}
