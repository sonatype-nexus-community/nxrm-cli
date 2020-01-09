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
package org.sonatype.nexus.cli.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.sonatype.nexus.cli.util.FileUtils;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.sonatype.nexus.cli.config.ConfigFileParser.*;
import static org.sonatype.nexus.cli.util.FileUtils.buildFullPath;
import static org.sonatype.nexus.cli.util.FileUtils.createFileIfNotExist;

public class ConfigFileUtils
{
  private static final String CONFIG_FILENAME = "config.json";

  private static final String CONFIG_DUMP_FILENAME = "config-dump.json";

  private ConfigFileUtils() {}

  public static String readConfigFile() {
    String homePath = FileUtils.getHomePath();
    Path configPath = Paths.get(buildFullPath(homePath, CONFIG_FILENAME));
    try {
      createFileIfNotExist(configPath);
      byte[] configFileContent = Files.readAllBytes(configPath);
      return new String(configFileContent);
    }
    catch (IOException ex) {
      throw new RuntimeException("Read/Write config file exception");
    }
  }

  public static void writeConfigFile(ConfigFile configFile) {
    String homePath = FileUtils.getHomePath();
    Path configPath = Paths.get(buildFullPath(homePath, CONFIG_FILENAME));
    Path configDumpPath = Paths.get(buildFullPath(homePath, CONFIG_DUMP_FILENAME));
    try {
      Files.copy(configPath, configDumpPath);
    }
    catch (IOException e) {
      throw new RuntimeException("Can't make config file copy");
    }

    try {
      Files.write(configPath, parse(configFile).getBytes(), TRUNCATE_EXISTING);
    }
    catch (IOException e) {
      System.console().printf("Error in writing configuration file. Changes were not save !!!");
      try {
        Files.copy(configDumpPath, configPath);
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    finally {
      if (Files.exists(configDumpPath)) {
        try {
          Files.delete(configDumpPath);
        }
        catch (IOException e) {
          //Do nothing
        }
      }
    }
  }
}
