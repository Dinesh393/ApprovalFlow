
package model;

import java.math.BigDecimal;

public class Request {
    private int requestId;
    private int userId;
    private String title;
    private String description;
    private BigDecimal amount;
    private String status;

    public Request() {
        super();
    }

    public Request(int userId, String title, String description,
                   BigDecimal amount, String status) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.status = status;
    }

    public Request(int requestId, int userId, String title,
                   String description, BigDecimal amount, String status) {
        this(userId, title, description, amount, status);
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
