package hike.test;

public class RedisQueues {
	private static final String qname="Queue:main";
	private static final String eqname="Queue:error";
	private static final String sname="Set:userWithErrors";
	
	private static final String pushErrScript="redis.call('rpush','"+eqname+"',ARGV[2]) \n"
			+ "redis.call('sadd','"+sname+"',ARGV[1]) \n";
	
	private static final String popTaskScript="local task= redis.call('lpop','"+qname+"') \n"
			+ "if ( not task) \n"
			+ "then \n"
			+ "		return nil \n"
			+ "end \n"
			+ "local _,user= task:match('([^:]+):\"([0-9]+)\"(.+)') \n"
			+ "if (redis.call('sismember','"+sname+"',user)==1) \n"
			+ "then \n"
			+ "		redis.call('rpush','"+eqname+"',task) \n"
			+ "		return 'error' \n"
			+ "else \n"
			+ "		return task \n"
			+ "end \n";
	
	private static final String popErrScript="local err= redis.call('lpop','"+eqname+"') \n"
			+ "if (not err) \n"
			+ "then \n"
			+ "		redis.call('del','"+sname+"') \n"
			+ "end \n"
			+ "return err \n";
	
	public static void pushTask(Task task){
		Redis.getInstance().rpush(qname, task.toString());
	}
	
	public static void pushError(Task err){
		Redis.getInstance().eval(pushErrScript, 0, String.valueOf(err.getUserId()), err.toString());
	}
	
	public static String checkAndPopTask(){
		return (String) Redis.getInstance().eval(popTaskScript, 0);
	}
	
	public static String popError(){
		return (String) Redis.getInstance().eval(popErrScript, 0);
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
