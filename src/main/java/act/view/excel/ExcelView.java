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

import act.app.App;
import act.app.event.AppEventId;
import act.event.AppEventListenerBase;
import act.util.ActContext;
import act.view.Template;
import act.view.TemplatePathResolver;
import act.view.View;
import org.osgl.http.H;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.S;
import osgl.version.Version;

import java.net.URL;
import java.util.EventObject;
import java.util.List;
import java.util.Set;

public class ExcelView extends View {

    public static final Version VERSION = Version.of(ExcelView.class);

    public static final String ID = "excel";

    public static final Set<H.Format> SUPPORTED_FORMATS = C.setOf(H.Format.XLS, H.Format.XLSX);

    @Override
    public String name() {
        return ID;
    }

    @Override
    protected void init(final App app) {
        TemplatePathResolver.registerSupportedFormats(SUPPORTED_FORMATS);
        app.eventBus().bind(AppEventId.PRE_START, new AppEventListenerBase() {
            @Override
            public void on(EventObject event) throws Exception {
                app.getInstance(JexlFunctionLoader.class).load();
            }
        });
    }

    @Override
    public boolean appliedTo(ActContext context) {
        return SUPPORTED_FORMATS.contains(context.accept());
    }

    @Override
    protected Template loadTemplate(String resourcePath) {
        URL url = ExcelView.class.getResource(S.fmt("/%s%s", ID, resourcePath));
        return null == url ? null : new ExcelTemplate(url);
    }

    @Override
    protected Template loadInlineTemplate(String content) {
        throw E.unsupport("Excel view does not support inline template");
    }

    public List<String> loadContent(String template) {
        throw E.unsupport();
    }

}
