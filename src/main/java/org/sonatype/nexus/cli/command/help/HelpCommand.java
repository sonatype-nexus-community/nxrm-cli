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
package org.sonatype.nexus.cli.command.help;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.sonatype.nexus.cli.CommandProvider;
import org.sonatype.nexus.cli.command.Command;

import static org.sonatype.nexus.cli.command.CommandOptionUtils.validateCommandOptionLeftovers;

public class HelpCommand
    implements Command
{
  public static final String COMMAND_HELP = "help";

  @Override
  public void validateAndSetOptions(final Map<String, String> options) {
    validateCommandOptionLeftovers(options);
  }

  @Override
  public void execute() {
    printHelp();
  }

  @Override
  public void printHelp() {
    CommandProvider.getInstance().registeredCommands.entrySet()
        .stream()
        .filter(entry -> !entry.getKey().equals(COMMAND_HELP))
        .map(Entry::getValue)
        .map(Supplier::get)
        .forEach(Command::printHelp);
  }
}
