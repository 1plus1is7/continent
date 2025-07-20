package me.continent.research;

import java.util.List;

public class ResearchNode {
    private final String id;
    private final String effect;
    private final String cost;
    private final double goldCost;
    private final String time;
    private final List<String> prereq;
    private final String tree;
    private final int tier;

    public ResearchNode(String id, String effect, String cost, String time, List<String> prereq, String tree, int tier) {
        this.id = id;
        this.effect = effect;
        this.cost = cost;
        this.goldCost = parseGoldCost(cost);
        this.time = time;
        this.prereq = prereq;
        this.tree = tree;
        this.tier = tier;
    }

    private static double parseGoldCost(String cost) {
        if (cost == null) return 0;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*G").matcher(cost);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    public String getId() { return id; }
    public String getEffect() { return effect; }
    public String getCost() { return cost; }
    public double getGoldCost() { return goldCost; }
    public String getTime() { return time; }
    public List<String> getPrereq() { return prereq; }
    public String getTree() { return tree; }
    public int getTier() { return tier; }
}
