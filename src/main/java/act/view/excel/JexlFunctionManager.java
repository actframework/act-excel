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

import act.util.DestroyableBase;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

/**
 * Manage all {@link JexlFunc} Jexl functions
 */
@Singleton
public class JexlFunctionManager extends DestroyableBase {

    private Map<String, Object> functions = new HashMap<>();

    synchronized void register(String namespace, Object function) {
        if (functions.containsKey(namespace)) {
            warn("function namespace already registered: %s", namespace);
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
