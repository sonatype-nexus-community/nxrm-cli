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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.sonatype.nexus.cli.RepositoryFormat;
import org.sonatype.nexus.cli.command.Command;
import org.sonatype.nexus.cli.config.ConfigFile;
import org.sonatype.nexus.cli.config.ConfigFileParser;
import org.sonatype.nexus.cli.config.ConfigFileRepository;
import org.sonatype.nexus.cli.config.ConfigFileUtils;

import static org.sonatype.nexus.cli.command.config.ConfigCommandOption.ADD_REPO;
import static org.sonatype.nexus.cli.command.config.ConfigCommandOption.FORMAT;
import static org.sonatype.nexus.cli.command.config.ConfigCommandOption.HELP;
import static org.sonatype.nexus.cli.command.config.ConfigCommandOption.NEXUS_HOST;
import static org.sonatype.nexus.cli.command.config.ConfigCommandOption.PASSWORD;
import static org.sonatype.nexus.cli.command.config.ConfigCommandOption.REMOVE_REPO;
import static org.sonatype.nexus.cli.command.config.ConfigCommandOption.USER;

public class ConfigCommand
    implements Command
{
  public static final String COMMAND_CONFIG = "config";

  private ConfigCommandOption configCommandOption = null;

  private final Map<String, String> options = new HashMap<>();

  @Override
  public void validateAndSetOptions(final Map<String, String> options) {
    // TODO: Update validation and setting
    if (options.containsKey(HELP.getName())) {
      if (options.size() == 1) {
        configCommandOption = HELP;
      }
    }
    else if (options.containsKey(ADD_REPO.getName()) && options.containsKey(NEXUS_HOST.getName()) &&
        options.containsKey(FORMAT.getName()) &&
        ((options.size() == 3) || (options.containsKey(USER.getName())) &&
            ((options.size() == 4 || (options.containsKey(PASSWORD.getName()) && options.size() == 5))))) {
      configCommandOption = ADD_REPO;
    }
    else if (options.containsKey(REMOVE_REPO.getName()) && (options.size() == 1 ||
        (options.size() == 2 && options.containsKey(NEXUS_HOST.getName())))) {
      configCommandOption = REMOVE_REPO;
    }

    if (configCommandOption == null) {
      printHelp();
      throw new RuntimeException();
    }

    this.options.putAll(options);
  }

  @Override
  public void execute() {
    ConfigFile configFile = ConfigFileParser.parse(ConfigFileUtils.readConfigFile());

    switch (configCommandOption) {

      case ADD_REPO: {
        List<ConfigFileRepository> repositories = configFile.getRepositories();

        if (repositories.stream()
            .anyMatch(repository -> repository.getRepoName().equals(options.get(ADD_REPO.getName())))) {
          throw new RuntimeException("Repository with name " + options.get(ADD_REPO.getName()) + " already exist");
        }

        ConfigFileRepository configFileRepository =
            new ConfigFileRepository(options.get(ADD_REPO.getName()), options.get(
                NEXUS_HOST.getName()), RepositoryFormat.valueOf(options.get(FORMAT.getName()).toUpperCase()));
        if (options.containsKey(USER.getName())) {
          configFileRepository.setUser(options.get(USER.getName()));
          if (options.containsKey(PASSWORD.getName())) {
            configFileRepository.setPassword(options.get(PASSWORD.getName()));
          }
        }
        repositories.add(configFileRepository);
        ConfigFileUtils.writeConfigFile(configFile);
      }
      break;
      case REMOVE_REPO: {
        List<ConfigFileRepository> repositories = configFile.getRepositories();
        String repository = options.get(REMOVE_REPO.getName());
        Optional<ConfigFileRepository> removeRepo =
            repositories.stream().filter(repo -> repo.getRepoName().equals(repository)).findFirst();

        if (!removeRepo.isPresent()) {
          throw new RuntimeException("Repository '" + repository + "' does not exist in configuration");
        }

        repositories.remove(removeRepo.get());
        ConfigFileUtils.writeConfigFile(configFile);
      }
      break;
      case HELP:
      default:
        printHelp();
    }

    configCommandOption = null;
    options.clear();
  }

  @Override
  public void printHelp() {
    System.out.print("Command 'config' changes tool configuration\n");
    System.out.print("Possible 'config' command options:\n");
    for (ConfigCommandOption option : ConfigCommandOption.values()) {
      System.out.printf("%-30s %-50s %-20s %-60s\n", option.getName(), option.getDescription(),
          "Optional: " + option.isOptional(), option.getExample());
    }
  }
}
