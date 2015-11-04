package hike.test.set;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

import hike.test.Redis;

public class RedisAndLocalSet extends Set {
	private String sname;
	private final java.util.Set<String> localSet=new HashSet<String>();
	public static Semaphore lock=new Semaphore(1);
	
	public RedisAndLocalSet(String sname) {
		this.sname=sname;
		try {
			lock.acquire();
			localSet.addAll( Redis.getInstance().smembers("Set:"+sname) );
			lock.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean contains(String elem) {
		return localSet.contains(elem);
	}

	@Override
	public void add(String elem) {
		if(!localSet.contains(elem)){
			try {
				lock.acquire();
				Redis.getInstance().sadd("Set:"+sname, elem);
				localSet.add(elem);
				lock.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void clear() {
		try {
			lock.acquire();
			Redis.getInstance().del("Set:"+sname);
			localSet.clear();
			lock.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
