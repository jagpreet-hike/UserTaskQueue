package hike.test.set;

import java.util.HashSet;

import hike.test.Redis;
import hike.test.Task;

public class OnlyLocalSet extends Set {
	private java.util.Set<String> set=new HashSet<String>();
	
	public  OnlyLocalSet(String errorQueueKey) {
		synchronized(set){
			Redis.getInstance().lrange(errorQueueKey, 0, -1).forEach( (s)->{
				set.add(String.valueOf( Task.createTaskFromString(s).getUserId() ));
			} );
		}
	}

	@Override
	public boolean contains(String elem) {
		synchronized(set){
			return set.contains(elem);
		}
	}

	@Override
	public void add(String elem) {
		synchronized(set){
			set.add(elem);
		}
	}

	@Override
	public void clear() {
		synchronized(set){
			set.clear();
		}
	}

}
