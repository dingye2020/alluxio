/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.underfs.hdfs;

import alluxio.AlluxioURI;
import alluxio.Configuration;
import alluxio.PropertyKey;
import alluxio.underfs.UnderFileSystem;
import alluxio.underfs.UnderFileSystemFactory;

import com.google.common.base.Preconditions;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Factory for creating {@link HdfsUnderFileSystem}.
 *
 * It caches created {@link HdfsUnderFileSystem}s, using the scheme and authority pair as the key.
 */
@ThreadSafe
public final class HdfsUnderFileSystemFactory implements UnderFileSystemFactory {
  /**
   * Constructs a new {@link HdfsUnderFileSystemFactory}.
   */
  public HdfsUnderFileSystemFactory() {}

  @Override
  public UnderFileSystem create(String path, Object conf) {
    Preconditions.checkNotNull(path);
    return new HdfsUnderFileSystem(new AlluxioURI(path), conf);
  }

  @Override
  public boolean supportsPath(String path) {
    return path != null && isHadoopUnderFS(path);
  }

/**
   * Determines if the given path is on a Hadoop under file system
   *
   * To decide if a path should use the hadoop implementation, we check
   * {@link String#startsWith(String)} to see if the configured schemas are found.
   *
   * @param path the path in under filesystem
   * @return true if the given path is on a Hadoop under file system, false otherwise
   */
  public static boolean isHadoopUnderFS(final String path) {
    // TODO(hy): In Hadoop 2.x this can be replaced with the simpler call to
    // FileSystem.getFileSystemClass() without any need for having users explicitly declare the file
    // system schemes to treat as being HDFS. However as long as pre 2.x versions of Hadoop are
    // supported this is not an option and we have to continue to use this method.
    for (final String prefix : Configuration.getList(PropertyKey.UNDERFS_HDFS_PREFIXES, ",")) {
      if (path.startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }
}
