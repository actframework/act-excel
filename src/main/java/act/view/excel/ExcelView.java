package act.view.excel;

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

import java.net.URL;
import java.util.EventObject;
import java.util.List;
import java.util.Set;

public class ExcelView extends View {

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
    protected Template loadTemplate(String resourcePath, ActContext context) {
        URL url = ExcelView.class.getResource(S.fmt("/%s%s", ID, resourcePath));
        return null == url ? null : new ExcelTemplate(url);
    }

    public List<String> loadContent(String template) {
        throw E.unsupport();
    }

}
