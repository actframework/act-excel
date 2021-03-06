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
