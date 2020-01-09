/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.cli.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.sonatype.nexus.cli.EnvironmentVariables;

public final class FileUtils
{
  private static final String DEFAULT_HOME_PATH = ".nexus-cli";

  private FileUtils() {
  }

  public static String getHomePath() {
    if (System.getenv().containsKey(EnvironmentVariables.NEXUS_CLI_HOME.toString())) {
      return System.getenv().get(EnvironmentVariables.NEXUS_CLI_HOME.toString());
    }
    return Paths.get(DEFAULT_HOME_PATH).toAbsolutePath().toString();
  }

  public static String buildFullPath(final String basePath, final String filename) {
    return basePath + File.separator + filename;
  }

  public static void createFileIfNotExist(final Path path) {
    try {
      if (Files.notExists(path)) {
        Files.createDirectories(path.getParent());
        Files.createFile(path);
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Error creating file or directory");
    }
  }
}
