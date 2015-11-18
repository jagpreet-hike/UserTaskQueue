package hike.test;

import java.util.List;

public class RedisQueues {
	private static final String qname="Queue:main";
	private static final String eqname="Queue:error";
	private static final String sname="Set:userWithErrors";
	
	private static final String pushErrScript="redis.call('rpush',KEYS[1],ARGV[2]) \n"
			+ "redis.call('sadd',KEYS[2],ARGV[1]) \n";
	
	private static final String popTaskScript="local task= redis.call('lpop',KEYS[1]) \n"
			+ "if ( not task) \n"
			+ "then \n"
			+ "		return nil \n"
			+ "end \n"
			+ "local _,user= task:match('([^:]+):\"([0-9]+)\"(.+)') \n"
			+ "if (redis.call('sismember',KEYS[3],user)==1) \n"
			+ "then \n"
			+ "		redis.call('rpush',KEYS[2],task) \n"
			+ "		return 'error' \n"
			+ "else \n"
			+ "		return task \n"
			+ "end \n";
	
	private static final String popErrScript="local err= redis.call('lpop',KEYS[1]) \n"
			+ "local delSet= 0 \n"
			+ "if (not err) \n"
			+ "then \n"
			+ "		delSet=1"
			+ "		redis.call('rename',KEYS[2],KEYS[2]..'old') \n"
			+ "end \n"
			+ "return {err,delSet} \n";
	
	public static void pushTask(Task task){
		Redis.getInstance().rpush(qname, task.toString());
	}
	
	public static void pushError(Task err){
		Redis.getInstance().eval(pushErrScript, 2,eqname,sname, String.valueOf(err.getUserId()), err.toString());
	}
	
	public static String checkAndPopTask(){
		return (String) Redis.getInstance().eval(popTaskScript, 3,qname,eqname,sname);
	}
	
	public static String popError(){
		List<Object> res=  (List<Object>) Redis.getInstance().eval(popErrScript, 2,eqname,sname);
		if( (long)res.get(1)==1){
			String newKey=sname+System.currentTimeMillis();
			Redis.getInstance().rename(sname+"old", newKey);
			Redis.getInstance().deleteSetInBatch(newKey);
		}
		return (String)res.get(0);
	}
	
	public static boolean isTaskQEmpty(){
		if(Redis.getInstance().llen(qname)==0)
			return true;
		return false;
	}
	
	public static boolean isErrQEmpty(){
		if(Redis.getInstance().llen(eqname)==0)
			return true;
		return false;
	}
}
