class interp {
    static void interp(Stm s) {
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
}
