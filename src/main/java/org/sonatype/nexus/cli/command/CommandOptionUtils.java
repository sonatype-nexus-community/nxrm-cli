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
package org.sonatype.nexus.cli.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.sonatype.nexus.cli.exception.OptionNotSetException;
import org.sonatype.nexus.cli.exception.UnknownOptionException;

public final class CommandOptionUtils
{
  private CommandOptionUtils() {
  }

  public static Map<String, String> parseCommandOptions(final String[] args) {
    final Map<String, String> optionsMap = new HashMap<>();
    for (String arg : args) {
      if (!arg.startsWith("--")) {
        throw new RuntimeException("Wrong argument: " + arg);
      }
      optionsMap.put(getCommandOptionName(arg), getCommandOptionValue(arg));
    }
    return optionsMap;
  }

  private static String getCommandOptionName(final String arg) {
    int indexOfEqualSign = arg.indexOf("=");
    if (indexOfEqualSign == -1) {
      return arg.substring(2);
    }
    return arg.substring(2, indexOfEqualSign);
  }

  private static String getCommandOptionValue(final String arg) {
    int indexOfEqualSign = arg.indexOf("=");
    if (indexOfEqualSign == -1) {
      return "";
    }
    return arg.substring(indexOfEqualSign + 1);
  }

  public static Optional<String> maybeGetAndRemoveCommandOption(final String key, final Map<String, String> options) {
    final String value = options.get(key);
    options.remove(key);
    return Optional.ofNullable(value);
  }

  public static void validateCommandOptionLeftovers(final Map<String, String> options) {
    if (!options.isEmpty()) {
      throw new UnknownOptionException(options.keySet());
    }
  }

  public static void validateAndSetCommandOptions(final CommandOption[] commandOptions,
                                                  final Map<String, String> optionsSource,
                                                  final Map<String, String> optionsTarget)
  {
    if (optionsSource.containsKey("help")) {
      optionsTarget.put("help", "");
      validateCommandOptionLeftovers(optionsSource);
    }
    else {
      Arrays.stream(commandOptions)
          .forEach(commandOption -> {
            final Optional<String> maybeCommandOptionValue
                = maybeGetAndRemoveCommandOption(commandOption.getName(), optionsSource);
            if (!maybeCommandOptionValue.isPresent()) {
              if (!commandOption.isOptional()) {
                throw new OptionNotSetException(commandOption);
              }
            }
            else {
              optionsTarget.put(commandOption.getName(), maybeCommandOptionValue.get());
            }
          });
      validateCommandOptionLeftovers(optionsSource);
    }
  }
}
