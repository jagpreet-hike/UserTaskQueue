package hike.test;

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
			String temp=q.pop();
			if(temp==null){
				onQEmpty.callback();
				while(temp==null)
					temp=q.pop(1);
			}
			while(temp!=null){
				Task t=Task.createTaskFromString(temp);
				if( !Common.processTask(t, failProb, isErrorConsumer) ){
					//fail
					onFail.callback(t);
				}
				temp=q.pop();
			}
		}
	}
}
