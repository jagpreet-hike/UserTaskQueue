package hike.test;

public class Set {
	private String sname;
	
	public Set(String sname){
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
