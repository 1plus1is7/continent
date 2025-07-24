package me.continent.enterprise.production;

/** Represents an active production job. */
public class ProductionJob {
    private final ProductionRecipe recipe;
    private final String enterpriseId;
    private final long startTime;
    private final long finishTime;
    private boolean collected;

    public ProductionJob(String enterpriseId, ProductionRecipe recipe, long startTime) {
        this.enterpriseId = enterpriseId;
        this.recipe = recipe;
        this.startTime = startTime;
        this.finishTime = startTime + recipe.getTime() * 1000L;
    }

    public ProductionRecipe getRecipe() {
        return recipe;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public boolean isComplete() {
        return System.currentTimeMillis() >= finishTime;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected() {
        this.collected = true;
    }
}
