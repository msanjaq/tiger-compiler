class Table {
    String id;
    int value;
    Table tail;
    Table(String i, int v, Table t){
        id = i;
        value = v;
        tail = t;
    }

    int lookup(String key){
        if(key == id)
            return value;
        return tail.lookup(key);
    }

    void print(){
        System.out.println(id + " " + value);
        if(tail != null)
            tail.print();
    }
}

class IntAndTable {
    int i;
    Table t;
    IntAndTable(int ii, Table tt) {
        i = ii;
        t = tt;
    }
}

class interp {
    static void interp(Stm s) {
        interpStm(s, null);
    }

    static int maxargs(Stm s) {
        if (s instanceof CompoundStm)
            return maxargsForCompoundStatements((CompoundStm) s);
        else if (s instanceof AssignStm)
            return maxargsForAssignStatements((AssignStm) s);
        else if (s instanceof PrintStm)
            return maxargsForPrintStatements((PrintStm) s);
        return 0;
    }

    public static void main(String args[]) throws java.io.IOException {
        System.out.println(maxargs(prog.prog));
        System.out.println(maxargs(prog.prog2));
        interp(prog.prog);
    }
        
    static Table interpStm(Stm s, Table t){
        if (s instanceof CompoundStm){
            Table cs = interpStm(((CompoundStm) s).stm1, t);
            return interpStm(((CompoundStm) s).stm2, cs);
        }
        else if (s instanceof AssignStm){
            AssignStm astm = (AssignStm) s;
            IntAndTable eval_exp = interpExp(astm.exp, t);
            return new Table(astm.id, eval_exp.i, eval_exp.t);
        }
        else if (s instanceof PrintStm)
            return eval_PrintStm((PrintStm)s, t);
        return null;
    }


    private static int maxargsForCompoundStatements(CompoundStm s){
        int leftMax = maxargs(s.stm1);
        int rightMax = maxargs(s.stm2);
        return leftMax > rightMax? leftMax : rightMax;
    }

    private static int maxargsForAssignStatements(AssignStm s){
        return maxArgsForStatementsInExpressions(s.exp);
    }

    private static int maxargsForPrintStatements(PrintStm s){
        int count = 1;    
        int nested_max = 0;
        int nested_count;
        ExpList expList = s.exps;  
        while(expList instanceof PairExpList)
        {
            PairExpList pList = (PairExpList) expList;
            nested_count = maxArgsForStatementsInExpressions(pList.head); 
            nested_max = nested_max > nested_count? nested_max : nested_count;
            expList = (ExpList)pList.tail;
            ++count; 
        }
        nested_count = maxArgsForStatementsInExpressions(((LastExpList) expList).head);
        nested_max = nested_max > nested_count? nested_max : nested_count;
        return count > nested_max ? count : nested_max;
    }
    
    private static int maxArgsForStatementsInExpressions(Exp exp){
        if (exp instanceof EseqExp)
            return maxargs(((EseqExp) exp).stm);
        return 0;
    }

    private static Table eval_PrintStm(PrintStm s, Table t){
        ExpList expList = s.exps;  
        Table nt = t;
        while(expList instanceof PairExpList)
        {
            PairExpList pList = (PairExpList) expList;
            IntAndTable val = interpExp(pList.head, t);
            System.out.print(val.i + " ");
            nt = val.t;
            expList = (ExpList)pList.tail;
        }
        IntAndTable val = interpExp(((LastExpList) expList).head, nt);
        System.out.println(val.i);
        return val.t;
    }

    private static IntAndTable interpExp(Exp e, Table t){
        if (e instanceof IdExp)
            return new IntAndTable(t.lookup(((IdExp)e).id), t);
        else if (e instanceof NumExp)
            return new IntAndTable(((NumExp)e).num, t);
        else if (e instanceof OpExp)
            return eval_opExp((OpExp)e, t);
        else if (e instanceof EseqExp)
            return eval_EseqExp((EseqExp) e, t);
        return null;
    }

    private static IntAndTable eval_EseqExp(EseqExp e, Table t){
        Table st = interpStm(e.stm, t);
        return interpExp(e.exp, t);
    }

    private static IntAndTable eval_opExp(OpExp e, Table t){
        IntAndTable left = interpExp(e.left, t);
        IntAndTable right = interpExp(e.right, left.t);
        switch(e.oper){
            case 1: return new IntAndTable(left.i + right.i, right.t);
            case 2: return new IntAndTable(left.i - right.i, right.t);
            case 3: return new IntAndTable(left.i * right.i, right.t);
            case 4: return new IntAndTable(left.i / right.i, right.t);
            default: return null;
        }
    }
}
