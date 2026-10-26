package model;

public class ApprovalStep {
	private int stepId;
	private int requestId;
	private int approverId;
	private String status;
	
	public ApprovalStep() {
		super();
	}
	public ApprovalStep(int requestId, int approverId, String status) {
		this();
		this.requestId = requestId;
		this.approverId = approverId;
		this.status = status;
	}
	public ApprovalStep(int stepId, int requestId, int approverId, String status) {
		this(requestId, approverId, status);
		this.stepId= stepId;
	}
	public int getStepId() {
		return stepId;
	}
	public void setStepId(int stepId) {
		this.stepId = stepId;
	}
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	public int getApproverId() {
		return approverId;
	}
	public void setApproverId(int approverId) {
		this.approverId = approverId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
