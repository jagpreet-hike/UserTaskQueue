package hike.test;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class Common {
	private static ConcurrentHashMap<Integer,Integer> lastCompletedTask = new ConcurrentHashMap<Integer,Integer>();
	private static ConcurrentHashMap<Integer,Integer> lastCreatedTask = new ConcurrentHashMap<Integer,Integer>();
	private static Set usersWithErrors=new Set("userWithErrors");
	
	public static Semaphore processingLock=new Semaphore(1);
	
	private static final int N=100;
	private static final float threshPer=0.7f;
	private static final int AVERAGE_PROCESSING_TIME = 1000;
	private static LinkedList<Boolean> lastNErrors=new LinkedList<Boolean>();
	private static int errorCount=0;
	
	private static final Random rand=new Random();
//	{
//		rand.setSeed(seed);
//	}
	
	private static boolean runTask(Task t,float failProb){
		System.out.println(getCurrentRateOfError());
		try {
			Thread.sleep(AVERAGE_PROCESSING_TIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(rand.nextFloat()>failProb)
		{
			onTaskSuccess(t);
			return true;
		}
		
		return false;
	}
	
	private static void onTaskSuccess(Task t){
		//success
		System.out.println("Success!!");
		checkTaskOrder(t);
		updateLastCompletedTask(t);
	}
	
	public static void checkTaskOrder(Task t){
		Integer prev=lastCompletedTask.get(t.getUserId());
		
		if(prev!=null && t.getTaskId()-prev!=1){
			System.err.println("Out Of Order for user"+String.valueOf(t.getUserId()));
			System.err.println("prev was: "+String.valueOf(prev) + " Current Task is: "+String.valueOf(t.getTaskId()) );
		}
	}
	
	public static void updateLastCompletedTask(Task t){
		lastCompletedTask.put(t.getUserId(),t.getTaskId());
	}
	
	public static Task createNextTask(int userId){
		Task t=new Task();
		t.setUserId(userId);
		
		Integer prev=lastCreatedTask.get(userId);
		if(prev==null)
			t.setTaskId(0);
		else
			t.setTaskId(prev+1);
		
		lastCreatedTask.put(userId, t.getTaskId());
		
		return t;
	}

	public static boolean hasUnresolvedError(int userId) {
		boolean ret=false;
		try {
			processingLock.acquire();
			if( usersWithErrors.contains( String.valueOf(userId) ) )
				ret= true;
			processingLock.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public static void newError(int userId) {
		try {
			processingLock.acquire();
			usersWithErrors.add( String.valueOf(userId) );
			processingLock.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void clearUsersWithErrors(){
		try {
			processingLock.acquire();
			usersWithErrors.clear();
			processingLock.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean processTask(Task t,float failProb,Boolean isErrorConsumer){
		boolean error=true;
		
		if( isErrorConsumer || !hasUnresolvedError(t.getUserId())  ){
			if(runTask(t,failProb)){
				error= false;
			}
		}
		
		if(!isErrorConsumer){
			
			if(error){
				if(getCurrentRateOfError()>=threshPer){
					while(!runTask(t, failProb));
					error= false;
				}
				else
					errorCount++ ;
			}
			
			lastNErrors.addFirst(error);
			
			if(lastNErrors.size()>N){
				if(lastNErrors.removeLast())
					errorCount--;
			}
		}
		
		return !error; //return if Operation was successful.
	}
	
	public static float getCurrentRateOfError(){
		return (float)(errorCount)/N;
	}
}