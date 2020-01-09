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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.sonatype.nexus.cli.command.Command;
import org.sonatype.nexus.cli.command.add.AddCommand;
import org.sonatype.nexus.cli.command.clear.ClearCommand;
import org.sonatype.nexus.cli.command.config.ConfigCommand;
import org.sonatype.nexus.cli.command.help.HelpCommand;
import org.sonatype.nexus.cli.command.status.StatusCommand;
import org.sonatype.nexus.cli.command.push.PushCommand;

import static org.sonatype.nexus.cli.command.add.AddCommand.COMMAND_ADD;
import static org.sonatype.nexus.cli.command.clear.ClearCommand.COMMAND_CLEAR;
import static org.sonatype.nexus.cli.command.config.ConfigCommand.COMMAND_CONFIG;
import static org.sonatype.nexus.cli.command.help.HelpCommand.COMMAND_HELP;
import static org.sonatype.nexus.cli.command.push.PushCommand.COMMAND_PUSH;
import static org.sonatype.nexus.cli.command.status.StatusCommand.*;

public final class CommandProvider
{
  private static final CommandProvider INSTANCE = new CommandProvider();

  public static CommandProvider getInstance() {
    return INSTANCE;
  }

  public final Map<String, Supplier<? extends Command>> registeredCommands;

  private CommandProvider() {
    final Map<String, Supplier<? extends Command>> commands = new HashMap<>();
    commands.put(COMMAND_HELP, HelpCommand::new);
    commands.put(COMMAND_CONFIG, ConfigCommand::new);
    commands.put(COMMAND_CLEAR, ClearCommand::new);
    commands.put(COMMAND_ADD, AddCommand::new);
    commands.put(COMMAND_PUSH, PushCommand::new);
    commands.put(COMMAND_STATUS, StatusCommand::new);
    this.registeredCommands = Collections.unmodifiableMap(commands);
  }

  public Command getCommandForName(final String name) {
    final Supplier<? extends Command> commandSupplier = registeredCommands.get(name.toLowerCase());
    if (commandSupplier == null) {
      throw new RuntimeException("Unknown command: " + name);
    }
    return commandSupplier.get();
  }
}
