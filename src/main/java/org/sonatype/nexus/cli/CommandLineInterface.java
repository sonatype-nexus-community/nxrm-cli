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
package org.sonatype.nexus.cli;

import java.util.Arrays;
import java.util.Map;

import org.sonatype.nexus.cli.command.Command;

import static org.sonatype.nexus.cli.command.CommandOptionUtils.parseCommandOptions;

public class CommandLineInterface
{
  //public static ConfigFile configFile;

  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        // Show help
        executeWithArguments(new String[]{"help"});
      }
      else {
        //configFile = ConfigFileParser.parse(ConfigFileUtils.readConfigFile());
        executeWithArguments(args);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void executeWithArguments(final String[] args) {
    final Command command = CommandProvider.getInstance().getCommandForName(args[0]);
    final Map<String, String> commandOptions = parseCommandOptions(Arrays.copyOfRange(args, 1, args.length));
    command.validateAndSetOptions(commandOptions);
    command.execute();
  }
}
