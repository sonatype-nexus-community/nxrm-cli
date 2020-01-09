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
package org.sonatype.nexus.cli.command.config;

import org.sonatype.nexus.cli.command.CommandOption;

import static org.sonatype.nexus.cli.command.config.ConfigCommand.COMMAND_CONFIG;

public enum ConfigCommandOption
    implements CommandOption
{
  NEXUS_HOST("nexus-host", false, "Nexus Host url", " --nexus-host=http://url"),
  FORMAT("format", false, "Repository format", " --format=npm"),
  USER("user", true, "Username for remote authentication", " --user=admin"),
  PASSWORD("password", true, "Password for remote authentication", " --password=123"),
  ADD_REPO("add-repo", false, "Add repository",
      COMMAND_CONFIG + " --add-repo=npm " + NEXUS_HOST.getExample() + USER.getExample() + PASSWORD.getExample()),
  REMOVE_REPO("remove-repo", false, "Remove repository",
      COMMAND_CONFIG + " --remove-repo=npm " + NEXUS_HOST.getExample()),
  HELP("help", true, "List of available options", COMMAND_CONFIG + " --help");

  private final String name;

  private final boolean optional;

  private final String description;

  private final String example;

  ConfigCommandOption(final String name, final boolean optional, final String description, final String example) {
    this.name = name;
    this.optional = optional;
    this.description = description;
    this.example = example;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isOptional() {
    return optional;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getExample() {
    return example;
  }

  @Override
  public String toString() {
    return getName();
  }
}
