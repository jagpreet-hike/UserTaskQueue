package hike.test;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
	
	private static final int maxUser=4;

	public static void main(String[] args) {
		
		Common.init();
		Queue q= new Queue("main");
		Queue eq= new Queue("error");
		
		QConsumer cons=new QConsumer(q, 0.10f,new QConsumer.TaskFailCallback() {
			@Override
			void callback(Task t) {
				synchronized (eq) {
//					System.out.println("Error in Main Queue, task: "+t);
					eq.push(t.toString());
					eq.notifyAll();
					Common.newError(t.getUserId());
				}
			}
		}, new QConsumer.OnQEmptyCallback(){
			@Override
			void callback() {
//				System.out.println("Main Q Empty");
			}
		},false);
		
		QConsumer econs=new QConsumer(eq, 0.08f,new QConsumer.TaskFailCallback() {
			@Override
			void callback(Task t) {
				while(!Common.processTask(t, 0.08f, true));
			}
		}, new QConsumer.OnQEmptyCallback(){
			@Override
			void callback() {
//				System.out.println("Error Queue Empty!");
				Common.clearUsersWithErrors();
			}
		},true);
		
		Timer producer=new Timer();
		producer.schedule(new TimerTask(){
			Random rand=new Random();
			@Override
			public void run() {
				int user=rand.nextInt(maxUser);
				Task t=Common.createNextTask(user);
				synchronized(q){
					q.push(t.toString());
					q.notifyAll();
				}
			}
		}, 100, 100);
		
		cons.start();
		econs.start();
		
	}

}
