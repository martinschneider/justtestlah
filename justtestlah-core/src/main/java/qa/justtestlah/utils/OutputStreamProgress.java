package qa.justtestlah.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Extension of {@link OutputStreamProgress} to enable a progress report for file uploads.
 *
 * <p>taken from:
 * https://stackoverflow.com/questions/7057342/how-to-get-a-progress-bar-for-a-file-upload-with-apache-httpclient-4#8475006
 */
public class OutputStreamProgress extends OutputStream {

  private final OutputStream outstream;
  private AtomicLong bytesWritten = new AtomicLong(0);

  /** @param outstream {@link OutputStream} */
  public OutputStreamProgress(OutputStream outstream) {
    this.outstream = outstream;
  }

  @Override
  public void write(int b) throws IOException {
    outstream.write(b);
    bytesWritten.incrementAndGet();
  }

  @Override
  public void write(byte[] b) throws IOException {
    outstream.write(b);
    bytesWritten.addAndGet(b.length);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    outstream.write(b, off, len);
    bytesWritten.addAndGet(len);
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
    return bytesWritten.get();
  }
}
