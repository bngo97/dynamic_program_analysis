public class NodeStatementFor extends NodeStatement {

    NodeStatement statement1;
    NodeCondition condition;
    NodeStatement statement2;
    NodeStatement statement3;

    public NodeStatementFor(
            NodeStatement s1,
            NodeCondition c,
            NodeStatement s2,
            NodeStatement s3) {
        this.statement1 = s1;
        this.condition = c;
        this.statement2 = s2;
        this.statement3 = s3;
    }

    @Override
    public void accept(Visitor v) {

    }

    @Override
    public String toString() {
        return "for statement" +
                "\nstatement1: " + (statement1 == null ? "null" : statement1.toString()) +
                "\ncondition: " + (condition == null ? "null" : condition.toString()) +
                "\nstatement2: " + (statement2 == null ? "null" : statement2.toString()) +
                "\nstatement3: " + statement3.toString();
    }
}
