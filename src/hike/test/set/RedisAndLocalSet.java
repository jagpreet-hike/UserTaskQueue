package hike.test.set;

import java.util.HashSet;

import hike.test.Redis;

public class RedisAndLocalSet extends Set {
	private String sname;
	private final java.util.Set<String> localSet=new HashSet<String>();
	
	public RedisAndLocalSet(String sname) {
		this.sname=sname;
		synchronized(localSet){
			localSet.addAll( Redis.getInstance().smembers("Set:"+sname) );
		};
	}
	
	@Override
	public boolean contains(String elem) {
		return localSet.contains(elem);
	}

	@Override
	public void add(String elem) {
		synchronized(localSet){
			localSet.add(elem);
		}
		Redis.getInstance().sadd("Set:"+sname, elem);
	}

	@Override
	public void clear() {
		synchronized(localSet){
			localSet.clear();
		}
		Redis.getInstance().del("Set:"+sname);
	}

}
