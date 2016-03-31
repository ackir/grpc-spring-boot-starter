package org.grpc.spring.boot.autoconfigure.component;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.health.Health;

import java.util.List;

import static io.netty.channel.ChannelHandler.Sharable;

/**
 * @author tolkv
 * @since 13/03/16
 */
@Sharable
public class HttpHealthCheckInboundHandler extends SimpleChannelInboundHandler<HttpRequest> {
  private final List<Endpoint<?>> endpoints;
  private Endpoint<Health> healthEndpoint;


  public HttpHealthCheckInboundHandler(List<Endpoint<?>> endpoints) {
    this.endpoints = endpoints;
    endpoints.stream().forEach(endpoint -> {
      if (endpoint instanceof HealthEndpoint) {
        healthEndpoint = (HealthEndpoint) endpoint;
      }
    });
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
    Health invoke = healthEndpoint.invoke();

    ByteBuf buffer = Unpooled.buffer(255);

    buffer.writeBytes("{".getBytes());
    buffer.writeBytes("\"status\": \"".getBytes());
    buffer.writeBytes(invoke.getStatus().getCode().getBytes());
    buffer.writeBytes("\",".getBytes());

    buffer.writeBytes("\"description\": \"".getBytes());
    buffer.writeBytes(invoke.getStatus().getDescription().getBytes());
    buffer.writeBytes("\"".getBytes());
    buffer.writeBytes("}".getBytes());


    final FullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK,
        buffer
    );
    if (ctx.channel().isWritable())
      ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    else
      System.out.println(" =notwriteable= ");

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }
}
