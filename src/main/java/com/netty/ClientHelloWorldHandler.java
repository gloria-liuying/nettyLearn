package netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
/**
 * <p>  </p>
 *
 * @author ly
 * @since 2018/12/10
 */
public class ClientHelloWorldHandler extends ChannelHandlerAdapter {
    public void channelRead(ChannelHandlerContext ctx,Object msg)throws Exception{
        try{
            ByteBuf readBuffer = (ByteBuf)msg;
            byte[] tempDatas = new byte[readBuffer.readableBytes()];
            readBuffer.readBytes(tempDatas);
            System.out.println("from server :"+new String(tempDatas,"UTF-8"));
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
    //异常处理逻辑，当客户端退出时，也会运行
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        System.out.println("client exceptionCaught method run ...");
        ctx.close();
    }
}
