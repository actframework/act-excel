package act.view.excel;

import act.util.DestroyableBase;
import org.osgl.logging.LogManager;
import org.osgl.logging.Logger;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage all {@link JexlFunc} Jexl functions
 */
@Singleton
public class JexlFunctionManager extends DestroyableBase {

    private static final Logger LOGGER = LogManager.get(JexlFunctionManager.class);

    private Map<String, Object> functions = new HashMap<>();

    synchronized void register(String namespace, Object function) {
        if (functions.containsKey(namespace)) {
            LOGGER.warn("function namespace already registered: %s", namespace);
            return;
        }
        functions.put(namespace, function);
    }

    public Map<String, Object> functions() {
        return functions;
    }

    @Override
    protected void releaseResources() {
        functions.clear();
    }

}
