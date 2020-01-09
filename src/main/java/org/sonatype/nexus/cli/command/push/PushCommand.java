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

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.sonatype.nexus.cli.command.Command;
import org.sonatype.nexus.cli.config.ConfigFileRepository;

import static org.sonatype.nexus.cli.command.CommandFileUtils.readFileBytes;
import static org.sonatype.nexus.cli.command.CommandFileUtils.readFirstLineFromChangelistFile;
import static org.sonatype.nexus.cli.command.CommandFileUtils.removeFirstLineFromChangelistFile;
import static org.sonatype.nexus.cli.command.CommandOptionUtils.validateAndSetCommandOptions;
import static org.sonatype.nexus.cli.command.push.PushCommandOption.CHANGELIST_NAME;
import static org.sonatype.nexus.cli.command.push.PushCommandOption.HELP;
import static org.sonatype.nexus.cli.command.push.PushCommandOption.REPO_NAME;
import static org.sonatype.nexus.cli.command.push.PushCommandOption.TAG;
import static org.sonatype.nexus.cli.config.ConfigFileParser.parse;
import static org.sonatype.nexus.cli.config.ConfigFileUtils.readConfigFile;
import static org.sonatype.nexus.cli.util.ApiUtils.createTagIfNotExist;
import static org.sonatype.nexus.cli.util.ApiUtils.uploadNexusFileAndSetTag;

public class PushCommand
    implements Command
{
  public static final String COMMAND_PUSH = "push";

  private Map<String, String> commandOptions = new HashMap<>();

  @Override
  public void validateAndSetOptions(final Map<String, String> receivedCommandOptions) {
    validateAndSetCommandOptions(PushCommandOption.values(), receivedCommandOptions, commandOptions);
  }

  @Override
  public void execute() {
    if (commandOptions.containsKey(HELP.getName())) {
      printHelp();
    }
    else {
      final String repoName = commandOptions.get(REPO_NAME.getName());
      final String changelistName = commandOptions.get(CHANGELIST_NAME.getName());
      final String tag = commandOptions.get(TAG.getName());

      final ConfigFileRepository repository = parse(readConfigFile()).getRepositories()
          .stream()
          .filter(repo -> repoName.equals(repo.getRepoName()))
          .findFirst()
          .orElseThrow(() -> new RuntimeException("Repository with name " + repoName + " is not found!"));

      createTagIfNotExist(repository.getNexusHost(), tag, repository.getUser(), repository.getPassword());

      String filePathString = readFirstLineFromChangelistFile(changelistName);
      while (filePathString != null && !filePathString.isEmpty()) {
        final String filename = Paths.get(filePathString).getFileName().toString();
        final byte[] fileBytes = readFileBytes(filePathString);

        uploadNexusFileAndSetTag(repository.getNexusHost(), repoName, repository.getFormat(), tag, filename, fileBytes,
            repository.getUser(), repository.getPassword());

        removeFirstLineFromChangelistFile(changelistName);
        filePathString = readFirstLineFromChangelistFile(changelistName);
      }

    }
  }

  @Override
  public void printHelp() {
    System.out.print("Command 'push' uploads files to Nexus repository with specified tag\n");
    System.out.print("Possible 'push' command options:\n");
    for (PushCommandOption option : PushCommandOption.values()) {
      System.out.printf("%-30s %-50s %-20s %-60s\n", option.getName(), option.getDescription(),
          "Optional: " + option.isOptional(), option.getExample());
    }
  }
}
