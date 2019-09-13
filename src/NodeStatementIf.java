
public class NodeStatementIf extends NodeStatement {

    NodeCondition condition;
    NodeStatement statement1;
    NodeStatement statement2;

    public NodeStatementIf(NodeCondition condition, NodeStatement statement1, NodeStatement statement2) {
        this.condition = condition;
        this.statement1 = statement1;
        this.statement2 = statement2;
    }

    @Override
    public void accept(Visitor v) {

    }

    @Override
    public String toString() {
        return "statement if " + condition.toString() + "\nstatement1= " + statement1.toString()
                + "\nstatement2= " + (statement2 == null ? "null" : statement2.toString());
    }
}
