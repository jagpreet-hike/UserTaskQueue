package hike.test.set;

import hike.test.Redis;

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
		Redis.getInstance().del("Set:"+sname);
	}

}
