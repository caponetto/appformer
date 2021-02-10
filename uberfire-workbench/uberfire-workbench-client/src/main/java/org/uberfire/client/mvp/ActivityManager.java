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
package org.uberfire.client.mvp;

import java.util.Set;

import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.PlaceRequest;

/**
 * A facility for obtaining references to specific {@link Activity} instances and for enumerating or discovering all
 * available activities of a certain type (screens, editors, popup dialogs, and so on). Also responsible for shutting
 * down and releasing Activity instances when they are no longer needed.
 * <p>
 * Note that you may never need to use an ActivityManager. While used extensively within the framework, UberFire
 * application code rarely comes into direct contact with activities, which are essentially autogenerated wrappers
 * around classes annotated with {@link WorkbenchScreen}, {@link WorkbenchClientEditor}, and friends.
 * Most Activity-related tasks can be accomplished at arm's length through a {@link PlaceManager}.
 * <p>
 * If you do need an instance of ActivityManager in your application, obtain it using {@code @Inject}.
 * @see PlaceManager
 * @see Activity
 */
public interface ActivityManager {

    /**
     * Calls to {@link #getActivities(PlaceRequest)} with security checks enabled.
     */
    Set<Activity> getActivities(final PlaceRequest placeRequest);

    /**
     * Returns an active, accessible activity that can handle the given PlaceRequest. In case there are multiple
     * activities that can handle the given place request, one of them is chosen at random.
     * @param placeRequest
     * @return an activity that handles the given PlaceRequest, or null if no available activity can handle. <b>No
     * actual type checking is performed! If you guess the type wrong, you will have an instance of the
     * wrong type. The only truly "safe" type to guess is {@link Activity}.</b>.
     */
    boolean containsActivity(final PlaceRequest placeRequest);

    /**
     * Calls to as {@link #getActivity(Class, PlaceRequest)} with security checks enabled.
     */
    <T extends Activity> T getActivity(final Class<T> clazz,
                                       final PlaceRequest placeRequest);

    /**
     * Destroys the given Activity bean instance, making it eligible for garbage collection.
     * @param activity the activity instance to destroy. <b>Warning: do not use with instances of SplashScreenActivity. These
     * are ApplicationScoped and cannot be destroyed.</b>
     */
    void destroyActivity(final Activity activity);
}
