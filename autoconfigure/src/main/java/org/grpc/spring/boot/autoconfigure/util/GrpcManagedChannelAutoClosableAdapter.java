package org.grpc.spring.boot.autoconfigure.util;

import io.grpc.ManagedChannel;
import lombok.experimental.Delegate;

public class GrpcManagedChannelAutoClosableAdapter extends ManagedChannel implements AutoCloseable {
  @Delegate ManagedChannel managedChannel;

  public GrpcManagedChannelAutoClosableAdapter(ManagedChannel delegate) {
    managedChannel = delegate;
  }

  @Override
  public void close() throws Exception {
    managedChannel.shutdown();
  }

}
