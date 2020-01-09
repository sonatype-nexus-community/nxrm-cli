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
package org.sonatype.nexus.cli.command;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static org.sonatype.nexus.cli.util.FileUtils.buildFullPath;
import static org.sonatype.nexus.cli.util.FileUtils.createFileIfNotExist;
import static org.sonatype.nexus.cli.util.FileUtils.getHomePath;

public final class CommandFileUtils
{
  private static final String CHANGELIST_FILENAME_PATTERN = "/changelist/changelist-%s";

  private CommandFileUtils() {
  }

  public static List<String> readFilenames(final String stringPath, final String pattern)
  {
    final Path path = Paths.get(stringPath);
    try {
      if (Files.isRegularFile(path)) {
        return Collections.singletonList(stringPath);
      }
      else {
        final Pattern compiledPattern = pattern == null ? Pattern.compile(".*") : Pattern.compile(pattern);
        return Files.walk(path)
            .filter(Files::isRegularFile)
            .map(Path::toString)
            .filter(filePath -> compiledPattern.matcher(filePath).matches())
            .collect(Collectors.toList());
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Exception during searching file directory");
    }
    catch (PatternSyntaxException e) {
      throw new RuntimeException("Error compiling pattern: " + pattern);
    }
  }

  public static void writeFilePathsToChangelistFile(final List<String> filePaths, final String changelistName)
  {
    final Path changelistFilePath = Paths.get(getChangelistPath(changelistName));
    try {
      createFileIfNotExist(changelistFilePath);
      filePaths.removeAll(Files.readAllLines(changelistFilePath));
      String content = String.join("\n", filePaths);
      if (!content.isEmpty()) {
        Files.write(changelistFilePath, (content + "\n").getBytes(), StandardOpenOption.APPEND);
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Write changelist file exception");
    }
  }

  public static void clearChangelistFile(final String changelistName)
  {
    final Path changelistFilePath = Paths.get(getChangelistPath(changelistName));
    try {
      createFileIfNotExist(changelistFilePath);
      Files.write(changelistFilePath, "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }
    catch (IOException e) {
      throw new RuntimeException("Clear changelist file exception");
    }
  }

  public static String readFirstLineFromChangelistFile(final String changelistName)
  {
    final String changelistPath = getChangelistPath(changelistName);
    try {
      createFileIfNotExist(Paths.get(changelistPath));
      try (BufferedReader reader = new BufferedReader(new FileReader(changelistPath))) {
        return reader.readLine();
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Exception during searching file directory");
    }
  }

  public static void removeFirstLineFromChangelistFile(final String changelistName)
  {
    final Path path = Paths.get(getChangelistPath(changelistName));
    try {
      createFileIfNotExist(path);
      List<String> allLines = Files.readAllLines(path);
      final String content = allLines.stream().skip(1).collect(Collectors.joining("\n"));
      Files.write(path, content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }
    catch (IOException e) {
      throw new RuntimeException("Write changelist file exception");
    }
  }

  public static byte[] readFileBytes(final String path)
  {
    try {
      return Files.readAllBytes(Paths.get(path));
    }
    catch (IOException e) {
      throw new RuntimeException("Exception during searching file directory");
    }
  }

  public static String getSha1FromFileBytes(byte[] fileBytes) {
    try {
      final MessageDigest md = MessageDigest.getInstance("SHA-1");
      final Formatter formatter = new Formatter();
      for (byte b : md.digest(fileBytes)) {
        formatter.format("%02x", b);
      }
      return formatter.toString();
    }
    catch (NoSuchAlgorithmException e) {
      // It never happens
      throw new RuntimeException(e);
    }
  }

  private static String getChangelistPath(final String changelistName) {
    return buildFullPath(getHomePath(), String.format(CHANGELIST_FILENAME_PATTERN, changelistName.toLowerCase()));
  }
}
