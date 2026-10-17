package net.jrdemiurge.enigmaticdice.config;

public class ResourceEventConfig {
    public boolean enabled;
    public String requiredMod;
    public String item;
    public int amount;
    public int weight;
    public String nbt;
    public String chatMessage;

    public ResourceEventConfig(boolean enabled, String requiredMod, String item, int amount, int weight, String nbt, String chatMessage) {
        this.enabled = enabled;
        this.requiredMod = requiredMod;
        this.item = item;
        this.amount = amount;
        this.weight = weight;
        this.nbt = nbt;
        this.chatMessage = chatMessage;
    }
}
