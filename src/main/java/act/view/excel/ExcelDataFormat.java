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

import act.app.*;
import act.app.event.SysEventId;
import act.asm.*;
import act.util.*;
import org.osgl.util.S;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

/**
 * Mark on a field of a class to specify the Excel data format
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelDataFormat {
    /**
     * Specify the data format of the field
     */
    String value();

    class Plugin extends AppCodeScannerPluginBase {


        @Override
        public AppSourceCodeScanner createAppSourceCodeScanner(App app) {
            return null;
        }

        @Override
        public AppByteCodeScanner createAppByteCodeScanner(final App app) {
            final Manager manager = new Manager();
            app.jobManager().on(SysEventId.DEPENDENCY_INJECTOR_PROVISIONED, "ExcelDataFormat:registerManagerSingleton", new Runnable() {
                @Override
                public void run() {
                    app.registerSingleton(manager);
                }
            });
            return manager;
        }

        @Override
        public boolean load() {
            return true;
        }

    }

    @Singleton
    class Manager extends AppByteCodeScannerBase {

        private static final String EXCEL_DATA_FORMAT_DESC = Type.getType(ExcelDataFormat.class).getDescriptor();

        Map<String, String> fieldStyleLookup = new HashMap<>();

        @Override
        protected boolean shouldScan(String s) {
            return true;
        }

        @Override
        public ByteCodeVisitor byteCodeVisitor() {
            return new _ByteCodeVisitor();
        }

        @Override
        public void scanFinished(String s) {

        }

        private class _ByteCodeVisitor extends ByteCodeVisitor {

            @Override
            public FieldVisitor visitField(int access, final String fieldName, String desc, String signature, Object value) {
                FieldVisitor fv = super.visitField(access, fieldName, desc, signature, value);
                if (AsmTypes.isStatic(access)) {
                    return fv;
                }
                return new FieldVisitor(ASM5, fv) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                        AnnotationVisitor av = super.visitAnnotation(desc, visible);
                        if (S.eq(EXCEL_DATA_FORMAT_DESC, desc)) {
                            return new AnnotationVisitor(ASM5, av) {
                                @Override
                                public void visit(String name, Object value) {
                                    if ("value".equals(name)) {
                                        fieldStyleLookup.put(fieldName, S.string(value));
                                    }
                                }
                            };
                        } else {
                            return av;
                        }
                    }
                };
            }
        }
    }

}
