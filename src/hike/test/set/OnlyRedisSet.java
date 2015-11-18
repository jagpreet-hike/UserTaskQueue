package hike.test.set;

import hike.test.Redis;
import redis.clients.jedis.ScanResult;

public class OnlyRedisSet extends Set{
	private String sname;
	
	public OnlyRedisSet(String sname) {
		this.sname=sname;
	}
	
	
	public boolean contains(String elem){
		return Redis.getInstance().sismember("Set:"+sname, elem);
	}
	
	public void add(String elem){
		Redis.getInstance().sadd("Set:"+sname, elem);
	}
	
	public void clear(){
		Redis.getInstance().deleteSetInBatch("Set:"+sname);
	}


	@Override
	public boolean init() {
		return true;
	}

}
