package prereqchecker;

public class AdjNode {
    private String item; // prereq name (data portion of node)
    private AdjNode  next; // link to next node

    // Constructor
    public AdjNode(String item, AdjNode next) {
        this.item = item;
        this.next = next;
    }

    // Getter/Setter methods
    public String getPrereq() { return item; }
    public void setPrereq(String item) { this.item = item; }
    
    public AdjNode getNextAdjNode() { return next; }
    public void setNextAdjNode(AdjNode next) { this.next = next; }
}
