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
package org.sonatype.nexus.cli.command.status;

import java.util.List;
import java.util.Map;

import org.sonatype.nexus.cli.command.Command;
import org.sonatype.nexus.cli.util.FileUtils;

import static org.sonatype.nexus.cli.command.CommandFileUtils.readFileBytes;
import static org.sonatype.nexus.cli.command.CommandFileUtils.readFilenames;

public class StatusCommand
    implements Command
{
  public static final String COMMAND_STATUS = "status";

  @Override
  public void validateAndSetOptions(final Map<String, String> receivedCommandOptions) {
    if (!receivedCommandOptions.isEmpty()) {
      throw new RuntimeException("Command 'status' do not support additional options");
    }
  }

  @Override
  public void execute() {
    List<String> filenames = readFilenames(FileUtils.getHomePath(), ".*/changelist/.*");
    for (String fileName : filenames) {
      byte[] fileContent = readFileBytes(fileName);
      String[] file = fileName.split("/");
      System.out.println("=== " + file[file.length - 1] + " ===");
      System.out.println(new String(fileContent));
    }
  }

  @Override
  public void printHelp() {
    System.out.println("Command 'status' shows all changelist's content\n");
  }
}
