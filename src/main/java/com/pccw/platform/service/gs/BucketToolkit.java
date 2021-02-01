/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pccw.platform.service.gs;

import com.google.cloud.storage.Blob;
// [START storage_upload_file]
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BucketToolkit {

  public static void downloadObject(
      String projectId, String bucketName, String objectName, String destFilePath) {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    // The path to which the file should be downloaded
    // String destFilePath = "/local/path/to/file.txt";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    Blob blob = storage.get(BlobId.of(bucketName, objectName));
    blob.downloadTo(Paths.get(destFilePath));

    System.out.println(
        "Downloaded object "
            + objectName
            + " from bucket name "
            + bucketName
            + " to "
            + destFilePath);
  }

  public static void uploadObject(
      String projectId, String bucketName, String objectName, String filePath) throws IOException {
    // The ID of your GCP project
    // String projectId = "your-project-id";

    // The ID of your GCS bucket
    // String bucketName = "your-unique-bucket-name";

    // The ID of your GCS object
    // String objectName = "your-object-name";

    // The path to your file to upload
    // String filePath = "path/to/your/file"

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    BlobId blobId = BlobId.of(bucketName, objectName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

    System.out.println(
        "File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
  }

  /**
   * Signing a URL requires Credentials which implement ServiceAccountSigner. These can be set
   * explicitly using the Storage.SignUrlOption.signWith(ServiceAccountSigner) option. If you don't,
   * you could also pass a service account signer to StorageOptions, i.e.
   * StorageOptions().newBuilder().setCredentials(ServiceAccountSignerCredentials). In this example,
   * neither of these options are used, which means the following code only works when the
   * credentials are defined via the environment variable GOOGLE_APPLICATION_CREDENTIALS, and those
   * credentials are authorized to sign a URL. See the documentation for Storage.signUrl for more
   * details.
   */
  public static void generateV4GetObjectSignedUrl(
      String projectId, String bucketName, String objectName) throws StorageException {
    // String projectId = "my-project-id";
    // String bucketName = "my-bucket";
    // String objectName = "my-object";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    // Define resource
    BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build();

    URL url =
        storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());

    System.out.println("Generated GET signed URL:");
    System.out.println(url);
    System.out.println("You can use this URL with any user agent, for example:");
    System.out.println("curl '" + url + "'");
  }

  /**
   * Signing a URL requires Credentials which implement ServiceAccountSigner. These can be set
   * explicitly using the Storage.SignUrlOption.signWith(ServiceAccountSigner) option. If you don't,
   * you could also pass a service account signer to StorageOptions, i.e.
   * StorageOptions().newBuilder().setCredentials(ServiceAccountSignerCredentials). In this example,
   * neither of these options are used, which means the following code only works when the
   * credentials are defined via the environment variable GOOGLE_APPLICATION_CREDENTIALS, and those
   * credentials are authorized to sign a URL. See the documentation for Storage.signUrl for more
   * details.
   */
  public static void generateV4GPutObjectSignedUrl(
      String projectId, String bucketName, String objectName) throws StorageException {
    // String projectId = "my-project-id";
    // String bucketName = "my-bucket";
    // String objectName = "my-object";

    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

    // Define Resource
    BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build();

    // Generate Signed URL
    Map<String, String> extensionHeaders = new HashMap<>();
    extensionHeaders.put("Content-Type", "application/octet-stream");

    URL url =
        storage.signUrl(
            blobInfo,
            15,
            TimeUnit.MINUTES,
            Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
            Storage.SignUrlOption.withExtHeaders(extensionHeaders),
            Storage.SignUrlOption.withV4Signature());

    System.out.println("Generated PUT signed URL:");
    System.out.println(url);
    System.out.println("You can use this URL with any user agent, for example:");
    System.out.println(
        "curl -X PUT -H 'Content-Type: application/octet-stream' --upload-file my-file '"
            + url
            + "'");
  }

  public static void main(String[] args) throws IOException {
    //    uploadObject(
    //        "pccw-dev",
    //        "pccw-dev-ps-svc-partner-store",
    //        "avatar",
    //        "/home/dxiong/Downloads/download.jpeg");
    //
    //    downloadObject(
    //        "pccw-dev",
    //        "pccw-dev-ps-svc-partner-store",
    //        "avatar",
    //        "/home/dxiong/Downloads/myavatar.jpeg");

    generateV4GetObjectSignedUrl("pccw-dev", "pccw-dev-ps-svc-partner-store", "avatar");
  }
}
// [END storage_upload_file]
