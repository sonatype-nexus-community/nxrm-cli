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
package org.sonatype.nexus.cli.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.sonatype.nexus.cli.RepositoryFormat;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import static org.sonatype.nexus.cli.RepositoryFormat.YUM;

public final class ApiUtils
{
  private static final String REPOSITORY_UPLOAD_URL_FORMAT = "%srepository/%s/";

  private static final String COMPONENT_UPLOAD_URL_FORMAT = "%sservice/rest/v1/components?repository=%s";

  private static final String GET_TAG_URL_FORMAT = "%sservice/rest/v1/tags/%s";

  private static final String CREATE_TAG_URL_FORMAT = "%sservice/rest/v1/tags";

  private static final String ASSOCIATE_WITH_TAG_URL_FORMAT =
      "%sservice/rest/v1/tags/associate/%s?wait=false&repository=%s&sha1=%s";

  private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

  private static final String AUTHORIZATION_TYPE_BASIC = "Basic ";

  private static final String AUTHORIZATION_HEADER_FORMAT = "%s:%s";

  private static final String CREATE_TAG_BODY_FORMAT = "{\"name\":\"%s\"}";

  private static final String CONTENT_TYPE_HEADER = "Content-Type";

  private static final String CONTENT_TYPE_JSON = "application/json";

  private static final String CHARSET_UTF8 = "UTF-8";

  private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();

  private ApiUtils() {
  }

  public static void createTagIfNotExist(final String nexusHost,
                                         final String tag,
                                         final String username,
                                         final String password)
  {
    final String formattedNexusHost = addClosingHttpSlashIsMissing(nexusHost);
    final String getTagUrl = String.format(GET_TAG_URL_FORMAT, formattedNexusHost, tag);
    final String authorizationValue = getAuthorizationValue(username, password);

    try {
      final HttpGet getTagRequest = new HttpGet(getTagUrl);
      getTagRequest.addHeader(AUTHORIZATION_HEADER_NAME, authorizationValue);

      System.out.println("Checking if tag " + tag + " already exists");
      try (CloseableHttpResponse getTagResponse = HTTP_CLIENT.execute(getTagRequest)) {
        final int getTagResponseStatusCode = getTagResponse.getStatusLine().getStatusCode();
        if (getTagResponseStatusCode == 404) {
          System.out.println("Tag " + tag + " doesn't exist, creating");
          final String createTagUrl = String.format(CREATE_TAG_URL_FORMAT, formattedNexusHost);
          final HttpPost createTagRequest = new HttpPost(createTagUrl);
          final String createTagBody = String.format(CREATE_TAG_BODY_FORMAT, tag);
          createTagRequest.addHeader(AUTHORIZATION_HEADER_NAME, authorizationValue);
          createTagRequest.addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON);
          createTagRequest.setEntity(new StringEntity(createTagBody, CHARSET_UTF8));
          try (CloseableHttpResponse createTagResponse = HTTP_CLIENT.execute(createTagRequest)) {
            final int createTagResponseStatusCode = createTagResponse.getStatusLine().getStatusCode();
            if (createTagResponseStatusCode != 200) {
              throw new RuntimeException("Create tag response error with status code: " + createTagResponseStatusCode);
            }
            System.out.println("Tag " + tag + " successfully created");
          }
        }
        else if (getTagResponseStatusCode != 200) {
          throw new RuntimeException("Get tag response error with status code: " + getTagResponseStatusCode);
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Exception during tag check");
    }
  }

  public static void associateFileWithTag(final String nexusHost,
                                          final String repoName,
                                          final String tag,
                                          final String fileSha1,
                                          final String username,
                                          final String password)
  {
    final String formattedNexusHost = addClosingHttpSlashIsMissing(nexusHost);
    final String associateFileWithTagUrl =
        String.format(ASSOCIATE_WITH_TAG_URL_FORMAT, formattedNexusHost, tag, repoName, fileSha1);
    final String authorizationValue = getAuthorizationValue(username, password);

    System.out.println("Associating file with sha1 " + fileSha1 + " with tag " + tag);
    try {
      final HttpPost associateFileWithTagRequest = new HttpPost(associateFileWithTagUrl);
      associateFileWithTagRequest.addHeader(AUTHORIZATION_HEADER_NAME, authorizationValue);

      try (CloseableHttpResponse associateFileWithTagResponse = HTTP_CLIENT.execute(associateFileWithTagRequest)) {
        final int associateFileWithTagResponseStatusCode = associateFileWithTagResponse.getStatusLine().getStatusCode();
        if (associateFileWithTagResponseStatusCode != 200) {
          throw new RuntimeException(
              "Set file tag response error with status code: " + associateFileWithTagResponseStatusCode);
        }
        System.out.println(tag + " successfully set for file with sha1 " + fileSha1);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Exception during file upload");
    }
  }

  public static void uploadNexusFile(final String nexusHost,
                                     final String repoName,
                                     final byte[] fileBytes,
                                     final String username,
                                     final String password) {
    final String formattedNexusHost = addClosingHttpSlashIsMissing(nexusHost);
    final String uploadFileUrl = String.format(REPOSITORY_UPLOAD_URL_FORMAT, formattedNexusHost, repoName);
    final String authorizationValue = getAuthorizationValue(username, password);

    try {
      final HttpPost uploadFileRequest = new HttpPost(uploadFileUrl);
      uploadFileRequest.addHeader(AUTHORIZATION_HEADER_NAME, authorizationValue);
      uploadFileRequest.setEntity(new ByteArrayEntity(fileBytes));

      System.out.println("Uploading file...");
      try (CloseableHttpResponse uploadFileResponse = HTTP_CLIENT.execute(uploadFileRequest)) {
        final int uploadFileResponseStatusCode = uploadFileResponse.getStatusLine().getStatusCode();
        if (uploadFileResponseStatusCode != 201) {
          throw new RuntimeException("Upload file response error with status code: " + uploadFileResponseStatusCode);
        }
        System.out.println("File successfully uploaded!");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Exception during file upload");
    }
  }

  public static void uploadNexusFileAndSetTag(
      final String nexusHost,
      final String repoName,
      final RepositoryFormat repositoryFormat,
      final String tag,
      final String filename,
      final byte[] fileBytes,
      final String username,
      final String password)
  {
    final String formattedNexusHost = addClosingHttpSlashIsMissing(nexusHost);
    final String uploadFileUrl = String.format(COMPONENT_UPLOAD_URL_FORMAT, formattedNexusHost, repoName);
    final String authorizationValue = getAuthorizationValue(username, password);

    try {
      final HttpPost uploadFileRequest = new HttpPost(uploadFileUrl);
      uploadFileRequest.addHeader(AUTHORIZATION_HEADER_NAME, authorizationValue);

      MultipartEntityBuilder multipartBuilder = MultipartEntityBuilder.create();
      multipartBuilder.addTextBody(repositoryFormat.getName() + ".tag", tag);
      multipartBuilder.addBinaryBody(repositoryFormat.getName() + ".asset", fileBytes);

      if (repositoryFormat == YUM) {
        multipartBuilder.addTextBody("yum.asset.filename", filename);
      }
      multipartBuilder.setCharset(StandardCharsets.UTF_8);
      HttpEntity multipart = multipartBuilder.build();
      uploadFileRequest.setEntity(multipart);

      System.out.println("Uploading file...");
      try (CloseableHttpResponse uploadFileResponse = HTTP_CLIENT.execute(uploadFileRequest)) {
        final int uploadFileResponseStatusCode = uploadFileResponse.getStatusLine().getStatusCode();
        if (uploadFileResponseStatusCode != 204) {
          throw new RuntimeException("Upload file response error with status code: " + uploadFileResponseStatusCode);
        }
        System.out.println("File successfully uploaded!");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Exception during file upload");
    }
  }

  private static String addClosingHttpSlashIsMissing(final String url) {
    if (!url.endsWith("/")) {
      return url + "/";
    }
    return url;
  }

  private static String getAuthorizationValue(final String username, final String password) {
    final String authorizationValue = String.format(AUTHORIZATION_HEADER_FORMAT, username, password);
    return AUTHORIZATION_TYPE_BASIC + Base64.getEncoder().encodeToString(authorizationValue.getBytes());
  }
}
