package hike.test;


public class Task {
	private int userId;
	private int taskId;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	
	@Override
	public String toString() {
		return "{ \"user\":\"" + userId + "\", \"task\":\"" + taskId + "\"}";
	}
	public static Task createTaskFromString(String string) {
		String[] tokens=string.split("\"");
		if( tokens.length != 9 ){
			System.err.println("Invalid String Input!!");
			return null;
		}
		
		Task t=new Task();
		t.setUserId( Integer.parseInt(tokens[3]) );
		t.setTaskId( Integer.parseInt(tokens[7]) );
		
		return t;
	}

}
