package hike.test.set;

import java.util.HashSet;

import hike.test.Common;
import hike.test.Redis;
import hike.test.Task;

public class OnlyLocalSet extends Set {
	private java.util.Set<String> set=new HashSet<String>();
	private String errorQueueKey;
	
	public  OnlyLocalSet(String errorQueueKey) {;
		this.errorQueueKey=errorQueueKey;
	}

	@Override
	public boolean contains(String elem) {
		return set.contains(elem);
	}

	@Override
	public void add(String elem) {
		set.add(elem);
	}

	@Override
	public void clear() {
		set.clear();
	}

	@Override
	public boolean init() {
		try {
			Common.lock.acquire();
			
			int batchSize=1000;
			long len=Redis.getInstance().llen(errorQueueKey);
			for(long i=0;i<len;i+=batchSize)
			Redis.getInstance().lrange(errorQueueKey, i, i+batchSize).forEach( (s)->{
				set.add(String.valueOf( Task.createTaskFromString(s).getUserId() ));
			} );
			
			Common.lock.release();
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
