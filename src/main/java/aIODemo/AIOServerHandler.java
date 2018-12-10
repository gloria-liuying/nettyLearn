import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Scanner;

/**
 * <p>  </p>
 *
 * @author ly
 * @since 2018/10/7
 */
public class AIOServerHandler implements CompletionHandler<AsynchronousSocketChannel,AIOServer> {

    /**
     * 业务处理逻辑，当请求到来后，监听成功，应该做什么
     * 一定要实现的逻辑：为下一次客户端请求开启监听。即accept方法调用
     * result参数：与客户端直接建立联系的通道
     * 无论BIO，NIO，AIO一旦建立连接，两端是平等的。
     * result中有通道中的左右相关数据。如：OS操作系统准备好的读取数据缓存，或等待返回数据的缓存。
     */
    @Override
    public void completed(AsynchronousSocketChannel result, AIOServer attachment) {
        //处理下一次的客户端请求。类似递归逻辑
        attachment.getServerChannel().accept(attachment,this);
        doRead(result);
    }

    /**
     * 异常处理逻辑,当服务端代码出现异常时，做什么事情。
     * @param exc
     * @param attachment
     */
    @Override
    public void failed(Throwable exc, AIOServer attachment) {
        exc.printStackTrace();
    }

    //项目中服务器返回的结果应该根据客户端的请求数据计算得到，不是等待控制台输入。
    private void doWrite(AsynchronousSocketChannel result){
        try{
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            System.out.println("enter message send to client >");
            Scanner s = new Scanner(System.in);
            String line = s.nextLine();
            buffer.put(line.getBytes("UTF-8"));
            //复位
            buffer.flip();
            //write是一个异步操作。具体实现由os实现。可以增加get方法，实现阻塞，等待os的写操作结束。
            result.write(buffer);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    private void doRead(final AsynchronousSocketChannel channel){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        /**
         * 异步读操作，
         */
        channel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            /**
             * 业务处理逻辑.
             */
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                try{
                    System.out.println(attachment.capacity());
                    attachment.flip();
                    System.out.println("from client:" +new String(attachment.array(),"UTF-8"));
                    doWrite(channel);
                }catch(UnsupportedEncodingException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
            }
        });
    }
}
