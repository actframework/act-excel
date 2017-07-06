package act.view.excel;

import act.Act;
import org.osgl.inject.annotation.AnnotatedWith;

import javax.inject.Inject;
import java.util.List;

public class JexlFunctionLoader {

    @Inject
    @AnnotatedWith(JexlFunc.class)
    private List<Object> functions;

    public void load() {
        JexlFunctionManager mgr = Act.getInstance(JexlFunctionManager.class);
        for (Object func : functions) {
            JexlFunc anno = func.getClass().getAnnotation(JexlFunc.class);
            mgr.register(anno.value(), func);
        }
    }

}
