package hike.test;

public class Queue {
	private String qname;
	
	Queue(String qname){
		this.qname=qname;
	}
	
	public  void push(String value){
		Redis.getInstance().rpush("Queue:"+qname, value);
	}
	
	public void addToFront(String value){
		Redis.getInstance().lpush("Queue:"+qname, value);
	}
	
	public String pop(){
		return Redis.getInstance().lpop("Queue:"+qname);
	}
	
	public boolean isEmpty(){
		if(Redis.getInstance().llen("Queue:"+qname)==0)
			return true;
		return false;
	}
	
	public Long size(){
		return Redis.getInstance().llen("Queue:"+qname);
	}
}
