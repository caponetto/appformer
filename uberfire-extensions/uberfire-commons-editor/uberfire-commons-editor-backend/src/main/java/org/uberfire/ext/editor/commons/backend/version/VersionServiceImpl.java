/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.backend.version;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;


import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.backend.server.util.Paths.convert;
import static org.uberfire.java.nio.file.StandardCopyOption.REPLACE_EXISTING;


@ApplicationScoped
public class VersionServiceImpl
        implements VersionService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private VersionRecordService versionRecordService;

    @Inject
    private PathResolver pathResolver;

    @Inject
    private VersionUtil versionUtil;

    @Override
    public List<VersionRecord> getVersions(final Path path) {

        try {
            return versionRecordService.load(Paths.convert(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path getPathToPreviousVersion(String uri) {
        return convert(ioService.get(URI.create(uri)));
    }

    @Override
    public Path restore(final Path _path,
                        final String comment,
                        final String branchName) {
        try {
            ioService.startBatch(Paths.convert(_path).getFileSystem());

            final org.uberfire.java.nio.file.Path path = pathResolver.resolveMainFilePath(convert(_path));
            final org.uberfire.java.nio.file.Path target = versionUtil.getPath(path, branchName);

            return convert(ioService.copy(path,
                                          target,
                                          REPLACE_EXISTING,
                                          new CommentedOption(
                                                  sessionInfo != null ? sessionInfo.getId() : "--",
                                                  sessionInfo != null ? sessionInfo.getIdentity().getIdentifier() : "system",
                                                  null,
                                                  comment)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ioService.endBatch();
        }
    }
}
