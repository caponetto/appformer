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
package org.uberfire.client.workbench.panels;

import java.util.Collection;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PartDefinition;

/**
 * An undecorated panel that can contain one part at a time and does not support child panels. The part's view fills
 * the entire panel. Adding a new part replaces the existing part. Does not support drag-and-drop rearrangement of
 * parts.
 */
@Dependent
public class WorkbenchPanelPresenterImpl extends AbstractWorkbenchPanelPresenter<WorkbenchPanelPresenterImpl> {

    private PlaceManager placeManager;

    @Inject
    public WorkbenchPanelPresenterImpl(final WorkbenchPanelViewImpl view,
                                       final PlaceManager placeManager) {
        super(view);
        this.placeManager = placeManager;
    }

    @Override
    protected WorkbenchPanelPresenterImpl asPresenterType() {
        return this;
    }

    /**
     * Returns null (static panels don't support child panels).
     */
    @Override
    public String getDefaultChildType() {
        return null;
    }

    @Override
    public void addPart(WorkbenchPartPresenter part) {
        if (getPanelView().getParts().isEmpty()) {
            super.addPart(part);
        } else {
            placeManager.closePlace(getPlaceFromFirstPart(),
                                    () -> super.addPart(part));
        }
    }

    private PlaceRequest getPlaceFromFirstPart() {
        Collection<PartDefinition> parts = getPanelView().getParts();
        if (parts.iterator().hasNext()) {
            PartDefinition part = parts.iterator().next();
            if (part != null) {
                return part.getPlace();
            }
        }
        return null;
    }
}
