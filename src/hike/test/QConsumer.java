package hike.test;

import java.util.Random;

public class QConsumer extends Thread {
	
	public static abstract class TaskFailCallback{
		abstract void callback(Task t);
	}
	
	public static abstract class OnQEmptyCallback{
		abstract void callback();
	}
	
	private Queue q;
	private float failProb;
	private TaskFailCallback onFail;
	private OnQEmptyCallback onQEmpty;
	private boolean isErrorConsumer;
	
	public QConsumer(Queue q,float failProb, TaskFailCallback onFail,OnQEmptyCallback onQEmtpy,boolean isErrorConsumer){
		super();
		this.q=q;
		this.failProb=failProb;
		this.onFail=onFail;
		this.onQEmpty=onQEmtpy;
		this.isErrorConsumer=isErrorConsumer;
	}
	
	@Override
	public void run(){
		while(true){
			Task t;
			synchronized(q){
				while(q.isEmpty()){
					onQEmpty.callback();
					try {
						q.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				t=Task.createTaskFromString(q.pop());
			}
			
			if( !Common.processTask(t, failProb, isErrorConsumer) ){
				//fail
				onFail.callback(t);
			}
		}
	}
}
