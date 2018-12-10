package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * <p>  </p>
 *
 * @author ly
 * @since 2018/12/9
 */
public class ServerHelloWorldHandler extends ChannelHandlerAdapter{

    /**
     * 业务处理逻辑
     * 用于处理读取数据请求的逻辑
     * @param ctx 上下文对象，其中包含客户端建立的所有资源，如：对应的Channel
     * @param msg 读取到的数据，默认类型为ByteBuf,是Netty自定义的。是对ByteBufer的封装。不需要考虑复位问题
     */
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
        ByteBuf readBuffer = (ByteBuf) msg;
        //创建一个字节数组，用于保存缓存中的数据
        byte[] tempDatas = new byte[readBuffer.readableBytes()];
        readBuffer.readBytes(tempDatas);
        String message = new String(tempDatas,"UTF-8");
        System.out.println("from client :"+message);
        if("exit".equals(message)){
            ctx.close();
            return;
        }
        String line = "server message to client";
        //写入自动释放缓存，避免内存溢出。如果是write，不会刷新缓存，数据不会发送到客户端，必须调用flush
        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")));
    }
    //异常处理逻辑，当客户端退出时，也会运行
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        System.out.println("server exceptionCaught method run ...");
        ctx.close();
    }
}
