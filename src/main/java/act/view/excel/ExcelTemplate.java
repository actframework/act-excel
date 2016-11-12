package act.view.excel;

import act.app.ActionContext;
import act.view.TemplateBase;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.osgl.$;
import org.osgl.http.H;
import org.osgl.mvc.result.ServerError;
import org.osgl.util.E;
import org.osgl.util.IO;
import org.osgl.util.S;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

class ExcelTemplate extends TemplateBase {

    private URL resource;

    ExcelTemplate(URL url) {
        this.resource = $.notNull(url);
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
            String name = context.paramVal("filename");
            if (S.blank(name)) {
                name = S.afterLast(context.actionPath(), ".");
                name = S.fmt("%s.%s", name, format.name());
            }
            response.contentDisposition(name, false);
        }
    }

    @Override
    protected void merge(Map<String, Object> renderArgs, H.Response response) {
        Context context = new Context(renderArgs);
        InputStream is = IO.is(resource);
        try {
            JxlsHelper.getInstance().processTemplate(is, response.outputStream(), context);
        } catch (IOException e) {
            throw new ServerError(e, "Error processing excel template: %s", resource.getPath());
        }
    }

    @Override
    protected String render(Map<String, Object> map) {
        throw E.unsupport();
    }
}
