package hike.test.set;

import java.util.HashSet;

import hike.test.Common;
import hike.test.Redis;
import redis.clients.jedis.ScanResult;

public class RedisAndLocalSet extends Set {
	private String sname;
	private final java.util.Set<String> localSet=new HashSet<String>();
	
	public RedisAndLocalSet(String sname) {
		this.sname=sname;
	}
	
	@Override
	public boolean contains(String elem) {
		return localSet.contains(elem);
	}

	@Override
	public void add(String elem) {
		localSet.add(elem);
		Redis.getInstance().sadd("Set:"+sname, elem);
	}

	@Override
	public void clear() {
		localSet.clear();
		Redis.getInstance().deleteSetInBatch("Set:"+sname);
	}

	@Override
	public boolean init() {
		try {
			Common.lock.acquire();
			
			String cursor="0";
			do{
				ScanResult<String> res=Redis.getInstance().sscan("Set:"+sname,cursor);
				localSet.addAll( res.getResult() );
				cursor=res.getStringCursor();
			}while(!cursor.equals("0"));
			
			Common.lock.release();
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
