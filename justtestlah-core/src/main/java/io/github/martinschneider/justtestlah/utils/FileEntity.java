package io.github.martinschneider.justtestlah.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Extension of {@link org.apache.http.entity.FileEntity} to enable a progress report for file
 * uploads.
 *
 * <p>taken from
 * https://stackoverflow.com/questions/7057342/how-to-get-a-progress-bar-for-a-file-upload-with-apache-httpclient-4#8475006
 */
public class FileEntity extends org.apache.http.entity.FileEntity {

  private OutputStreamProgress outstream;

  public FileEntity(File file) {
    super(file);
  }

  @Override
  public void writeTo(OutputStream outstream) throws IOException {
    this.outstream = new OutputStreamProgress(outstream);
    super.writeTo(this.outstream);
  }

  /** @return the progress of the upload (0-100) */
  public int getProgress() {
    if (outstream == null) {
      return 0;
    }
    long contentLength = getContentLength();
    if (contentLength <= 0) { // Prevent division by zero and negative values
      return 0;
    }
    long writtenLength = outstream.getWrittenLength();
    return (int) (100 * writtenLength / contentLength);
  }
}
