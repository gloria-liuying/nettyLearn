package RedisMq;

import redis.RedisPool;
import redis.clients.jedis.Jedis;
import java.util.concurrent.TimeUnit;

/**
 * <p>  </p>
 *
 * @author ly
 * @since 2019/1/5
 */
public class Producer extends Thread{

    public static final String MESSAGE_KEY = "queue";
    private Jedis jedis;
    private String produceName;
    private volatile int count;

    public Producer(String name){
        this.produceName = name;
        init();
    }
    private void init(){
        jedis = RedisPool.getResource(1);

    }
    public void putMessage(String message) {
        Long size = jedis.lpush(MESSAGE_KEY, message);
        System.out.println(produceName + ": 当前未被处理消息条数为:" + size);
        count++;
    }

    public int getCount() {
        return count;
    }
    @Override
    public void run() {
        try {
            while (true) {
                putMessage("hello world");
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Producer producer = new Producer("myProducer");
        producer.start();

        for (; ; ) {
            System.out.println("main : 已存储消息条数:" + producer.getCount());
            TimeUnit.SECONDS.sleep(10);
        }
    }
}
