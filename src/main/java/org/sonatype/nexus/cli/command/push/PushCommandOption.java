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
package org.sonatype.nexus.cli.command.push;

import org.sonatype.nexus.cli.command.CommandOption;

import static org.sonatype.nexus.cli.command.push.PushCommand.COMMAND_PUSH;

public enum PushCommandOption
    implements CommandOption
{
  REPO_NAME("repo-name", false, "Set repository name", COMMAND_PUSH + " --repo-name=maven-remote"),
  CHANGELIST_NAME("changelist-name", false, "Set changelist name",
      COMMAND_PUSH + " --changelist-name=maven-remote-files"),
  TAG("tag", false, "Set tag for uploaded files",
      COMMAND_PUSH + " --tag=customTag"),
  HELP("help", true, "List of available options", COMMAND_PUSH + " --help");

  private final String name;

  private boolean optional;

  private final String description;

  private final String example;

  PushCommandOption(final String name, final boolean optional, final String description, final String example) {
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
}
