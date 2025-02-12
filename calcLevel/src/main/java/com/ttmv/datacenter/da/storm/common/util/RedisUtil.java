package com.ttmv.datacenter.da.storm.common.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

public class RedisUtil {
	private static Logger logger = LogManager.getLogger(RedisUtil.class);

	public static Jedis getRedis() throws Exception {
		return new Jedis("dars_comu_1", 51313);
	}
	
	

	/***
	 * 编写redis lua脚本批量获取redis数据
	 * 
	 * @throws Exception
	 */

	@SuppressWarnings("unchecked")
	public static List<Object> getValueBatch(String key, int number) throws Exception {
		Object result = null;
		List<String> keys = new ArrayList<String>();
		keys.add(key);
		List<String> argv = new ArrayList<String>();
		argv.add(String.valueOf(number));
		String luaScript = " local list= {} " + " local num = redis.call('llen',KEYS[1])"
				+ " if num == 0 then return nil end" + " for i=1,ARGV[1],1 do "
				+ "    local value = redis.call('lpop',KEYS[1]) " + "    if value then" + "        list[i] = value "
				+ "    else " + "        break" + "    end" + " end " + " if #list<1 then " + "    return nil "
				+ " end " + " return list";

		try {
			Jedis jedis = getRedis();
			result = jedis.eval(luaScript, keys, argv);
			jedis.close();
			return (List<Object>) result;
		} catch (Exception e) {
			throw e;
		}

	}

	public static void pushLevelExpValue2Dams(String key, String value) throws Exception {
		Jedis jedis = null;
		try {
			jedis = getRedis();
			jedis.rpush(key, new String[] { value });
			jedis.close();
		} catch (Exception e) {
			throw e;
		}

	}
	
	public static void pushLevelExpValue2Im(String key, String value) throws Exception {
		Jedis jedis = null;
		try {
			jedis = getRedis();
			logger.info("[redis队列] ==> 发送数据"+value+"到"+key+"队列");
			jedis.rpush(key, new String[] { value });
			jedis.close();
		} catch (Exception e) {
			throw e;
		}

	}
	
	
	

}
