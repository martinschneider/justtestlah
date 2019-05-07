package io.github.martinschneider.justtestlah.awsdevicefarm;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.devicefarm.AWSDeviceFarm;
import com.amazonaws.services.devicefarm.AWSDeviceFarmClient;
import com.amazonaws.services.devicefarm.AWSDeviceFarmClientBuilder;
import com.amazonaws.services.devicefarm.model.AWSDeviceFarmException;
import com.amazonaws.services.devicefarm.model.CreateUploadRequest;
import com.amazonaws.services.devicefarm.model.GetUploadRequest;
import com.amazonaws.services.devicefarm.model.GetUploadResult;
import com.amazonaws.services.devicefarm.model.Upload;
import com.amazonaws.services.devicefarm.model.UploadType;
import io.github.martinschneider.justtestlah.utils.FileEntity;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service class to interact with AWS, basically a wrapper around {@link AWSDeviceFarm}. */
public class AWSService {

  private static final Logger LOG = LoggerFactory.getLogger(AWSService.class);

  private AWSDeviceFarm aws;

  /** Constructor */
  public AWSService() {
    aws = buildClient();
  }

  /**
   * Constructor
   *
   * @param awsConfiguration key-value map holding AWS configuration
   */
  public AWSService(Map<String, String> awsConfiguration) {

    if (awsConfiguration == null) {
      // use the default credentials provider
      aws = buildClient();
    } else {
      String awsAccessKey = awsConfiguration.get("accessKey");
      String awsSecretKey = awsConfiguration.get("secretKey");
      String awsRegion = awsConfiguration.get("awsRegion");
      if (awsAccessKey == null || awsSecretKey == null || awsRegion == null) {
        // if any of the values is not set we use the default credentials provider
        aws = buildClient();
      } else {
        aws = buildClient(awsAccessKey, awsSecretKey, awsRegion);
      }
    }
  }

  /**
   * Constructor
   *
   * @param awsAccessKey the AWS access key
   * @param awsSecretKey the AWS secret key
   * @param awsRegion the AWS region
   */
  public AWSService(String awsAccessKey, String awsSecretKey, String awsRegion) {
    aws = buildClient(awsAccessKey, awsSecretKey, awsRegion);
  }

  /** @return {@link AWSDeviceFarm} */
  public AWSDeviceFarm getAws() {
    return aws;
  }

  private AWSDeviceFarm buildClient() {
    LOG.debug("Building AWS Device Farm client using default credentials provider");
    return AWSDeviceFarmClientBuilder.standard().build();
  }

  private AWSDeviceFarm buildClient(String awsAccessKey, String awsSecretKey, String awsRegion) {
    LOG.debug("Building AWS Device Farm client");
    LOG.debug("awsAccessKey={}", awsAccessKey);
    LOG.debug("awsSecretKey={}", awsSecretKey);
    LOG.debug("awsRegion={}", awsRegion);
    return AWSDeviceFarmClient.builder()
        .withCredentials(
            new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
        .withRegion(awsRegion)
        .build();
  }

  /**
   * Upload a file to AWS Device Farm (modified from
   * https://github.com/awslabs/aws-device-farm-jenkins-plugin)
   *
   * @param file the file to upload
   * @param projectArn the ARN of the Device Farm project
   * @param uploadType the {@link UploadType}
   * @param synchronous true, if the execution should wait for the download to succeed
   * @return {@link Upload}
   * @throws InterruptedException {@link InterruptedException}
   * @throws IOException {@link IOException}
   * @throws AWSDeviceFarmException {@link AWSDeviceFarmException}
   */
  public Upload upload(File file, String projectArn, UploadType uploadType, Boolean synchronous)
      throws InterruptedException, IOException, AWSDeviceFarmException {
    CreateUploadRequest appUploadRequest =
        new CreateUploadRequest()
            .withName(UUID.randomUUID() + "_" + file.getName())
            .withProjectArn(projectArn)
            .withContentType("application/octet-stream")
            .withType(uploadType.toString());
    Upload upload = aws.createUpload(appUploadRequest).getUpload();

    CloseableHttpClient httpClient = HttpClients.createSystem();
    HttpPut httpPut = new HttpPut(upload.getUrl());
    httpPut.setHeader("Content-Type", upload.getContentType());

    FileEntity entity = new FileEntity(file);
    httpPut.setEntity(entity);

    LOG.debug("AWS S3 upload URL: {}", upload.getUrl());

    Thread thread =
        new Thread() {
          public void run() {
            HttpResponse response = null;
            try {
              response = httpClient.execute(httpPut);
            } catch (IOException exception) {
              throw new AWSDeviceFarmException(
                  String.format("Error uploading file to AWS: %s", exception.getMessage()));
            }
            if (response.getStatusLine().getStatusCode() != 200) {
              throw new AWSDeviceFarmException(
                  String.format(
                      "Upload returned non-200 responses: %d",
                      response.getStatusLine().getStatusCode()));
            }
          }
        };
    thread.start();
    int progress = 0;
    while (thread.isAlive()) {
      int newProgress = entity.getProgress();
      if (newProgress > progress) {
        LOG.info("{}% completed {}", progress, file.getAbsolutePath());
        progress = newProgress;
      }
      Thread.sleep(500);
    }

    if (synchronous) {
      while (true) {
        GetUploadRequest describeUploadRequest = new GetUploadRequest().withArn(upload.getArn());
        GetUploadResult describeUploadResult = aws.getUpload(describeUploadRequest);
        String status = describeUploadResult.getUpload().getStatus();

        if ("SUCCEEDED".equalsIgnoreCase(status)) {
          LOG.info("Uploading {} succeeded: {}", file.getName(), describeUploadRequest.getArn());
          break;
        } else if ("FAILED".equalsIgnoreCase(status)) {
          LOG.info(
              "Error message from device farm: '{}'",
              describeUploadResult.getUpload().getMetadata());
          throw new AWSDeviceFarmException(String.format("Upload %s failed!", upload.getName()));
        } else {
          try {
            LOG.info(
                "Waiting for upload {} to be ready (current status: {})", file.getName(), status);
            Thread.sleep(5000);
          } catch (InterruptedException exception) {
            LOG.info("Thread interrupted while waiting for the upload to complete");
            throw exception;
          }
        }
      }
    }
    return upload;
  }
}
