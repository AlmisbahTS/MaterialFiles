/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.materialfilemanager.filesystem;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.zhanghai.android.materialfilemanager.functional.Functional;

public class Files {

    private Files() {}

    @NonNull
    public static File ofUri(@NonNull Uri uri) {
        switch (uri.getScheme()) {
            case LocalFile.SCHEME:
                return new LocalFile(uri);
            case ArchiveFile.SCHEME:
                return new ArchiveFile(uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @NonNull
    public static File ofLocalPath(@NonNull String path) {
        return new LocalFile(LocalFile.uriFromPath(path));
    }

    @NonNull
    public static File childOf(@NonNull File file, @NonNull String name) {
        Uri uri = file.getUri().buildUpon().appendPath(name).build();
        return ofUri(uri);
    }

    public static void onTrailChanged(@NonNull List<File> path) {
        Set<java.io.File> archiveJavaFiles = Functional.map(
                Functional.filter(path, file -> file instanceof ArchiveFile),
                file -> ((ArchiveFile) file).getArchiveFile().makeJavaFile(), new HashSet<>());
        Archive.retainCache(archiveJavaFiles);
    }

    public static void invalidateCache(@NonNull File file) {
        if (file instanceof ArchiveFile) {
            ArchiveFile archiveFile = (ArchiveFile) file;
            java.io.File archiveJavaFile = archiveFile.getArchiveFile().makeJavaFile();
            Archive.invalidateCache(archiveJavaFile);
        }
    }
}
