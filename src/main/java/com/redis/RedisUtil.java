package redis;

import org.nustaq.serialization.FSTConfiguration;
/**
 * <p>  </p>
 *
 * @author ly
 * @since 2019/1/5
 */
public final class RedisUtil {

    public final static int TOKENTIMEOUT = 24*60*60;  //token24小时过期
    public final static int CODETIMEOUT = 60*5;  //验证码5分钟过期
   // private static FSTConfiguration configuration = FSTConfiguration.createConfiguration();
    private RedisUtil(){};

}
