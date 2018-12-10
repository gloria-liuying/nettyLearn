import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * <p>  </p>
 *
 * @author ly
 * @since 2018/10/7
 */
public class AIOClient {

    private AsynchronousSocketChannel channel;

    public AIOClient(String host,int port){
        init(host,port);
    }

    private void init(String host,int port){
        try{
            channel = AsynchronousSocketChannel.open();
            channel.connect(new InetSocketAddress(host,port));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void write(String line){
       try{
           ByteBuffer buffer = ByteBuffer.allocate(1024);
           buffer.put(line.getBytes("UTF-8"));
           buffer.flip();
           //write方法是一个异步操作，具体实现由os实现。可增加get方法，实现阻塞，等待OS的写操作结束
           channel.write(buffer);
       }catch (UnsupportedEncodingException e){
           e.printStackTrace();
       }
    }

    public void read(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try{
            //get为阻塞
            channel.read(buffer).get();
//            channel.read();
            buffer.flip();
            System.out.println("from server :"+new String(buffer.array(),"UTF-8"));
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch (ExecutionException e){
            e.printStackTrace();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    public void doDestory(){
        if(null !=channel){
            try{channel.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        AIOClient client = new AIOClient("localhost",9999);
        try{
            System.out.println("enter message send to server >");
            Scanner s = new Scanner(System.in);
            String line = s.nextLine();
            client.write(line);
            client.read();
        }finally {
            client.doDestory();
        }
    }
}
