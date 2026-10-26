package model;

public class ApprovalHistory {
	private int historyId;
	private int stepId;
	private String action;
	private String comments;
	
	public ApprovalHistory() {
		super();
	}
	public ApprovalHistory(int stepId, String action, String comments) {
		this();
		this.stepId = stepId;
		this.action = action;
		this.comments = comments;
	}
	
	public ApprovalHistory(int historyId, int stepId, String action, String comments) {
		this(stepId, action, comments);
		this.historyId = historyId;
	}
	
	public int getHistoryId() {
		return historyId;
	}
	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}
	public int getStepId() {
		return stepId;
	}
	public void setStepId(int stepId) {
		this.stepId = stepId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
}
