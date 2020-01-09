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
package org.sonatype.nexus.cli.command.clear;

import java.util.HashMap;
import java.util.Map;

import org.sonatype.nexus.cli.command.Command;

import static org.sonatype.nexus.cli.command.CommandFileUtils.clearChangelistFile;
import static org.sonatype.nexus.cli.command.CommandOptionUtils.validateAndSetCommandOptions;
import static org.sonatype.nexus.cli.command.add.AddCommandOption.CHANGELIST_NAME;
import static org.sonatype.nexus.cli.command.add.AddCommandOption.HELP;

public class ClearCommand
    implements Command
{
  public static final String COMMAND_CLEAR = "clear";

  private Map<String, String> commandOptions = new HashMap<>();

  @Override
  public void validateAndSetOptions(final Map<String, String> receivedCommandOptions) {
    validateAndSetCommandOptions(ClearCommandOption.values(), receivedCommandOptions, commandOptions);
  }

  @Override
  public void execute() {
    if (commandOptions.containsKey(HELP.getName())) {
      printHelp();
    }
    else {
      clearChangelistFile(commandOptions.get(CHANGELIST_NAME.getName()).toUpperCase());
    }
  }

  @Override
  public void printHelp() {
    System.out.print("Command 'clear' removes all data from specified changelist\n");
    System.out.print("Possible 'clear' command options:\n");
    for (ClearCommandOption option : ClearCommandOption.values()) {
      System.out.printf("%-30s %-50s %-20s %-60s\n", option.getName(), option.getDescription(),
          "Optional: " + option.isOptional(), option.getExample());
    }
  }
}
