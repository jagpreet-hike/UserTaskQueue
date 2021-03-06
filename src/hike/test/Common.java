package hike.test;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import hike.test.set.OnlyRedisSet;
import hike.test.set.Set;

public class Common {
	private static ConcurrentHashMap<Integer,Integer> lastCompletedTask = new ConcurrentHashMap<Integer,Integer>();
	private static ConcurrentHashMap<Integer,Integer> lastCreatedTask = new ConcurrentHashMap<Integer,Integer>();
	private static Set usersWithErrors=new OnlyRedisSet("userWithErrors");
	
	private static final int N=100;
	private static final float threshPer=0.7f;
	private static final int MAX_PROCESSING_TIME = 500;
	private static LinkedList<Boolean> lastNErrors=new LinkedList<Boolean>();
	private static int errorCount=0;
	
	private static final Random rand=new Random();

	public static Semaphore lock=new Semaphore(1);
	
//	{
//		rand.setSeed(seed);
//	}
	/*
	* This Function is called in main to make sure all static variables of this class are initialized before program starts processing tasks. 
	* (required for fetching data from redis when using OnlyLocalSet, before data is altered by consumers.)
	* Just a work-around for time being.
	*/
	public static void init(){ 
		rand.setSeed(System.currentTimeMillis());
		usersWithErrors.init();
		
		/*clearing redis just for proper working of simulation*/
		Redis.getInstance().del("Queue:error");
		Redis.getInstance().del("Queue:main");
		Redis.getInstance().del("Set:userWithErrors");
	}
	
	private static boolean runTask(Task t,float failProb){
		try {
			Thread.sleep(rand.nextInt(MAX_PROCESSING_TIME));
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
//		System.out.println("Success!!");
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
		if( usersWithErrors.contains( String.valueOf(userId) ) )
			return true;
		return false;
	}

	public static void newError(int userId) {
		usersWithErrors.add( String.valueOf(userId) );
	}
	
	public static void clearUsersWithErrors(){
		usersWithErrors.clear();
	}
	
	public static boolean processTask(Task t,float failProb,Boolean isErrorConsumer){
		boolean error=true;
		
		if( isErrorConsumer || !hasUnresolvedError(t.getUserId())  ){
			if(runTask(t,failProb)){
				error= false;
			}
		}
		
		if(!isErrorConsumer){
			System.out.println(getCurrentRateOfError());

			if(error){
				if(getCurrentRateOfError()>=threshPer){
					while(hasUnresolvedError(t.getUserId()) || !runTask(t, failProb));
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
