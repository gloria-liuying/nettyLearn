package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

/**
 * <p>  </p>
 * 请求的发起者，不需要监听。只需要定义唯一的线程组。
 * @author ly
 * @since 2018/12/10
 */
public class ClientHelloWorld {
    private EventLoopGroup group = null;
    private Bootstrap bootstrap = null;
    public ClientHelloWorld(){
        init();
    }
    private void init(){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
    }
    public ChannelFuture doRequest(String host, int port, final ChannelHandler... handlers)throws Exception{
        /**
         *在客户端必须绑定处理器，就是必须调用handler方法
         * 服务器必须绑定服务器，调用childHandler方法
         */
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch)throws Exception{
                ch.pipeline().addLast(handlers);
            }
        });
        ChannelFuture future = this.bootstrap.connect(host,port).sync();
        return future;
    }
    public void release(){
        this.group.shutdownGracefully();
    }

    public static void main(String[] args) {
        ClientHelloWorld client = null;
        ChannelFuture future = null;
        try{
            client = new ClientHelloWorld();
            future = client.doRequest("localhost",9999,new ClientHelloWorldHandler());
            Scanner s = null;
            while (true){
                s = new Scanner(System.in);
                System.out.println("enter message send to server enter 'exit' for close client");
                String line = s.nextLine();
                if("exit".equals(line)){
                    future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")))
                            .addListener(ChannelFutureListener.CLOSE);
                    break;
                }
                future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")));
                TimeUnit.SECONDS.sleep(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null!=future){
                try{
                    future.channel().closeFuture().sync();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }if(null!=client){
                client.release();
            }
        }
    }
}
