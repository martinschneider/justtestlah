package io.github.martinschneider.justtestlah.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Extension of {@link OutputStreamProgress} to enable a progress report for file uploads.
 *
 * <p>taken from:
 * https://stackoverflow.com/questions/7057342/how-to-get-a-progress-bar-for-a-file-upload-with-apache-httpclient-4#8475006
 */
public class OutputStreamProgress extends OutputStream {

  private final OutputStream outstream;
  private volatile long bytesWritten = 0;

  public OutputStreamProgress(OutputStream outstream) {
    this.outstream = outstream;
  }

  @Override
  public void write(int b) throws IOException {
    outstream.write(b);
    bytesWritten++;
  }

  @Override
  public void write(byte[] b) throws IOException {
    outstream.write(b);
    bytesWritten += b.length;
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    outstream.write(b, off, len);
    bytesWritten += len;
  }

  @Override
  public void flush() throws IOException {
    outstream.flush();
  }

  @Override
  public void close() throws IOException {
    outstream.close();
  }

  public long getWrittenLength() {
    return bytesWritten;
  }
}
