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

import org.sonatype.nexus.cli.RepositoryFormat;

public class ConfigFileRepository
{
  static final String REPO_NAME_KEY = "name";
  static final String NEXUS_HOST_KEY = "nexus-host";

  static final String NEXUS_FORMAT_KEY = "format";
  static final String USER_KEY = "user";
  static final String PASSWORD_KEY = "password";

  private String repoName;

  private String nexusHost;

  private RepositoryFormat format;

  private String user = "";

  private String password = "";

  public ConfigFileRepository(final String repoName, final String nexusHost, final RepositoryFormat format) {
    this.repoName = repoName;
    this.nexusHost = nexusHost;
    this.format = format;
  }

  public String getRepoName() {
    return repoName;
  }

  public String getNexusHost() {
    return nexusHost;
  }

  public RepositoryFormat getFormat() {
    return format;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }

  public void setUser(final String user) {
    this.user = user;
  }

  public void setPassword(final String password) {
    this.password = password;
  }
}
