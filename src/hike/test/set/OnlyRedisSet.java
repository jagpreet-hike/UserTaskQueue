package hike.test.set;

import java.util.concurrent.Semaphore;

import hike.test.Redis;

public class OnlyRedisSet extends Set{
	private String sname;
	public static Semaphore lock=new Semaphore(1);
	
	public OnlyRedisSet(String sname) {
		this.sname=sname;
	}
	
	
	public boolean contains(String elem){
		boolean ret=false;
		try {
			lock.acquire();
			ret= Redis.getInstance().sismember("Set:"+sname, elem);
			lock.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public void add(String elem){
		try {
			lock.acquire();
			Redis.getInstance().sadd("Set:"+sname, elem);
			lock.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clear(){
		try {
			lock.acquire();
			Redis.getInstance().del("Set:"+sname);
			lock.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
