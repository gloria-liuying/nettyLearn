package RedisMq;

import redis.RedisPool;
import redis.clients.jedis.Jedis;
/**
 * <p>  </p>
 *
 * @author ly
 * @since 2019/1/7
 */


    /**
     * 消息消费者
     * @author yamikaze
     */
    public class Customer extends Thread{

        private String customerName;
        private volatile int count;
        private Jedis jedis;

        public Customer(String name) {
            this.customerName = name;
            init();
        }

        private void init() {
            jedis = RedisPool.getResource(1);
        }

        public void processMessage() {
            String message = jedis.rpop(Producer.MESSAGE_KEY);
            if(message != null) {
                count++;
                handle(message);
            }
        }

        public void handle(String message) {
            System.out.println(customerName + " 正在处理消息,消息内容是: " + message + " 这是第" + count + "条");
        }

        @Override
        public void run() {
            while (true) {
                processMessage();
            }
        }

        public static void main(String[] args) {
            Customer customer = new Customer("");
            customer.start();
        }
    }

