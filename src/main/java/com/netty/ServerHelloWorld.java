package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * <p>  </p>
 *
 * @author ly
 * @since 2018/10/20
 */
public class ServerHelloWorld {
    //监听线程组，监听客户端请求
    private EventLoopGroup acceptorGroup = null;
    //处理客户端相关线程组，负责处理与客户端的数据通讯
    private EventLoopGroup clientGroup = null;
    //服务启动相关配置信息
    private ServerBootstrap bootstrap = null;
    public ServerHelloWorld(){
        init();
    }
    private void init(){
        //初始化线程组
        acceptorGroup = new NioEventLoopGroup();
        clientGroup = new NioEventLoopGroup();
        //初始化服务的配置
        bootstrap = new ServerBootstrap();
        //绑定线程组
        bootstrap.group(acceptorGroup,clientGroup);
        //设置通讯模式为NIO，（同步非阻塞）
        bootstrap.channel(NioServerSocketChannel.class);
        //设置缓冲区大小，单位是字节
        bootstrap.option(ChannelOption.SO_BACKLOG,1024);
        //SO_SNDBUF发送缓冲区，SO_RCVBUF接收缓冲区，SO_KEEPALIVE开启心跳检测（保证连接有效）
//        bootstrap.option(ChannelOption.SO_SNDBUF,16*1024)
//                .option(ChannelOption.SO_RCVBUF,16*1024)
//                .option(ChannelOption.SO_KEEPALIVE,true);
    }

    /**
     * 监听处理逻辑
     * @param port 监听端口
     * @param acceptorsHandlers：处理器，如何处理客户端请求
     * @return
     * @throws Exception
     */
    public ChannelFuture doAccept(int port, final ChannelHandler... acceptorsHandlers)throws Exception{
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch){
                ch.pipeline().addLast(acceptorsHandlers);
            }
        });
        ChannelFuture future = bootstrap.bind(port).sync().addListener(new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(Future<? super Void> future) {
                if (future.isSuccess()) {
                    System.out.println("端口绑定成功!");
                } else {
                    System.err.println("端口绑定失败!");
                }
            }
        });
        return future;
    }

    public void release(){
        this.acceptorGroup.shutdownGracefully();
        this.clientGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        ChannelFuture future = null;
        ServerHelloWorld server = null;
        try{
            server = new ServerHelloWorld();
            future = server.doAccept(9999,new ServerHelloWorldHandler());
            System.out.println("server started .");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null!=future){
                try{
                    future.channel().closeFuture().sync();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            if(null !=future){
                server.release();
            }
        }
    }
}
