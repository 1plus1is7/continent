package me.continent.enterprise.contract;

import java.util.UUID;

/** Information about a contract request. */
public class Contract {
    private final UUID id;
    private final String issuer;
    private final String description;
    private final String item;
    private final int amount;
    private final int reward;
    private final long dueTime;
    private ContractGrade grade;
    private ContractState state;
    private String enterpriseId;

    public Contract(UUID id, String issuer, String description, String item, int amount,
                    int reward, long dueTime, ContractGrade grade) {
        this.id = id;
        this.issuer = issuer;
        this.description = description;
        this.item = item;
        this.amount = amount;
        this.reward = reward;
        this.dueTime = dueTime;
        this.grade = grade;
        this.state = ContractState.AVAILABLE;
    }

    public UUID getId() { return id; }
    public String getIssuer() { return issuer; }
    public String getDescription() { return description; }
    public String getItem() { return item; }
    public int getAmount() { return amount; }
    public int getReward() { return reward; }
    public long getDueTime() { return dueTime; }
    public ContractGrade getGrade() { return grade; }
    public void setGrade(ContractGrade grade) { this.grade = grade; }
    public ContractState getState() { return state; }
    public void setState(ContractState state) { this.state = state; }
    public String getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(String id) { this.enterpriseId = id; }
}
