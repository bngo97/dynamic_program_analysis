import java.util.List;

public class NodeClassDecl extends NodeDecl {

    String parent;
    List<String> impls;
    List<NodeVarDecl> vars;
    List<NodeMethodDecl> methods;

    public NodeClassDecl(String id, String parent, List<String> impls, List<NodeVarDecl> vars, List<NodeMethodDecl> methods) {
        super(id);
        this.parent = parent;
        this.impls = impls;
        this.vars = vars;
        this.methods = methods;
    }

    @Override
    public void accept(Visitor v) {

    }

    @Override
    public String toString() {
        return "class " + id +" extends " + parent + " implements " + impls.toString() + "\nvars = "
                + vars.toString() + "\n" + methods.toString();
    }

}