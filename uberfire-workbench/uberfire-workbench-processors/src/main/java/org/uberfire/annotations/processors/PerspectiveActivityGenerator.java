/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.annotations.processors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.annotations.processors.facades.ClientAPIModule;

/**
 * Generates a Java source file enerator for Activities
 */
public class PerspectiveActivityGenerator extends AbstractGenerator {

    @Override
    public StringBuffer generate(final String packageName,
                                 final PackageElement packageElement,
                                 final String className,
                                 final Element element,
                                 final ProcessingEnvironment processingEnvironment) throws GenerationException {

        final Messager messager = processingEnvironment.getMessager();
        messager.printMessage(Kind.NOTE,
                              "Starting code generation for [" + className + "]");

        //Extract required information
        final TypeElement classElement = (TypeElement) element;
        String identifier = ClientAPIModule.getWbPerspectiveScreenIdentifierValueOnClass(classElement);

        final String getPerspectiveMethodName = GeneratorUtils.getPerspectiveMethodName(classElement,
                                                                                        processingEnvironment);
        if (GeneratorUtils.debugLoggingEnabled()) {
            messager.printMessage(Kind.NOTE,
                                  "Package name: " + packageName);
            messager.printMessage(Kind.NOTE,
                                  "Class name: " + className);
            messager.printMessage(Kind.NOTE,
                                  "Identifier: " + identifier);
            messager.printMessage(Kind.NOTE,
                                  "getPerspectiveMethodName: " + getPerspectiveMethodName);
        }

        Map<String, Object> root = new HashMap<>();

        //Setup data for FreeMarker
        root.put("packageName",
                 packageName);
        root.put("className",
                 className);
        root.put("identifier",
                 identifier);
        root.put("realClassName",
                 classElement.getSimpleName().toString());
        root.put("getPerspectiveMethodName",
                 getPerspectiveMethodName);

        //Generate code
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter(sw);
        try {
            final Template template = config.getTemplate("perspective.ftl");
            template.process(root,
                             bw);
        } catch (IOException ioe) {
            throw new GenerationException(ioe);
        } catch (TemplateException te) {
            throw new GenerationException(te);
        } finally {
            try {
                bw.close();
                sw.close();
            } catch (IOException ioe) {
                throw new GenerationException(ioe);
            }
        }
        messager.printMessage(Kind.NOTE,
                              "Successfully generated code for [" + className + "]");

        return sw.getBuffer();
    }
}
