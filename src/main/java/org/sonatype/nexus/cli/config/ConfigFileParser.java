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
package org.sonatype.nexus.cli.config;

import java.util.ArrayList;
import java.util.List;

import org.sonatype.nexus.cli.RepositoryFormat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import static org.sonatype.nexus.cli.config.ConfigFileRepository.NEXUS_FORMAT_KEY;
import static org.sonatype.nexus.cli.config.ConfigFileRepository.NEXUS_HOST_KEY;
import static org.sonatype.nexus.cli.config.ConfigFileRepository.PASSWORD_KEY;
import static org.sonatype.nexus.cli.config.ConfigFileRepository.REPO_NAME_KEY;
import static org.sonatype.nexus.cli.config.ConfigFileRepository.USER_KEY;

public class ConfigFileParser
{
  private static final String JSON_REPOSITORIES = "repositories";

  public static ConfigFile parse(String configFileContent) {
    if (configFileContent.isEmpty()) {
      return new ConfigFile();
    }
    JSONObject configObject = new JSONObject(new JSONTokener(configFileContent));
    ConfigFile configFile = new ConfigFile();

    if (configObject.keySet().contains(JSON_REPOSITORIES)) {
      JSONArray repositoriesJson = configObject.getJSONArray(JSON_REPOSITORIES);
      List<ConfigFileRepository> repositoryNames = new ArrayList<>();
      for (int index = 0; index < repositoriesJson.length(); index++) {
        JSONObject repositoryJson = repositoriesJson.getJSONObject(index);

        ConfigFileRepository repository =
            new ConfigFileRepository(repositoryJson.getString(REPO_NAME_KEY),
                repositoryJson.getString(NEXUS_HOST_KEY),
                RepositoryFormat.valueOf(repositoryJson.getString(NEXUS_FORMAT_KEY).toUpperCase()));

        if (repositoryJson.keySet().contains(USER_KEY)) {
          repository.setUser(repositoryJson.getString(USER_KEY));
        }

        if (repositoryJson.keySet().contains(PASSWORD_KEY)) {
          repository.setPassword(repositoryJson.getString(PASSWORD_KEY));
        }

        repositoryNames.add(repository);
      }

      configFile.getRepositories().addAll(repositoryNames);
    }

    return configFile;
  }

  public static String parse(ConfigFile configFile) {
    List<ConfigFileRepository> repositories = configFile.getRepositories();
    JSONArray repositoriesJson = new JSONArray();
    for (ConfigFileRepository repository : repositories) {

      JSONObject repositoryJson = new JSONObject();
      repositoryJson.put(REPO_NAME_KEY, repository.getRepoName());
      repositoryJson.put(NEXUS_HOST_KEY, repository.getNexusHost());
      repositoryJson.put(NEXUS_FORMAT_KEY, repository.getFormat().getName());

      if (!repository.getUser().isEmpty()) {
        repositoryJson.put(USER_KEY, repository.getUser());
        if (!repository.getPassword().isEmpty()) {
          repositoryJson.put(PASSWORD_KEY, repository.getPassword());
        }
      }
      repositoriesJson.put(repositoryJson);
    }

    JSONObject repositoriesObject = new JSONObject();
    repositoriesObject.put(JSON_REPOSITORIES, repositoriesJson);

    return repositoriesObject.toString();
  }
}
