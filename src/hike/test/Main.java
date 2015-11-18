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
				try {
					Common.lock.acquire();
//					System.out.println("Error in Main Queue, task: "+t);
					
					//TODO retry with expnonetail exception
					eq.push(t.toString());
					Common.newError(t.getUserId());
					
					Common.lock.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				try {
					Common.lock.acquire();
					Common.clearUsersWithErrors();
					Common.lock.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},true);
		
		Timer producer=new Timer();
		producer.schedule(new TimerTask(){
			Random rand=new Random();
			@Override
			public void run() {
				int user=rand.nextInt(maxUser);
				Task t=Common.createNextTask(user);
				q.push(t.toString());
			}
		}, 100, 100);
		
		cons.start();
		econs.start();
		
	}

}
