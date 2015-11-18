package hike.test;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
	
	private static final int maxUser=4;

	public static void main(String[] args) {
		
		Common.init();
		
		Thread cons=new Thread(new Runnable(){
			private static final float failProb=0.10f;
			
			@Override
			public void run() {
				while(true){
					String temp;
					do{
						try {
//							cons.wait();
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}while(RedisQueues.isTaskQEmpty());
					
					temp=RedisQueues.checkAndPopTask();
					while(temp!=null){
						if(temp.equals("error")){
							temp=RedisQueues.checkAndPopTask();
							continue;
						}
						Task t=Task.createTaskFromString(temp);
						if( !Common.processTask(t, failProb, false) ){
							//fail
							RedisQueues.pushError(t);
						}
						temp=RedisQueues.checkAndPopTask();
					}
				}
			}
		});
		
		Thread econs=new Thread(new Runnable(){
			private static final float failProb=0.08f;
			
			@Override
			public void run() {
				while(true){
					String temp;
					do{
						try {
//							econs.wait();
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}while(RedisQueues.isErrQEmpty());
					
					temp=RedisQueues.popError();
					while(temp!=null){
						Task t=Task.createTaskFromString(temp);
						if( !Common.processTask(t, failProb, true) ){
							//fail
							while(!Common.processTask(t, failProb, true));
						}
						temp=RedisQueues.popError();
					}
				}
			}
		});
		
		Timer producer=new Timer();
		producer.schedule(new TimerTask(){
			Random rand=new Random();
			@Override
			public void run() {
				int user=rand.nextInt(maxUser);
				Task t=Common.createNextTask(user);
				RedisQueues.pushTask(t);
			}
		}, 10, 10);
		
		cons.start();
		econs.start();
		
	}

}
