package ece351.vhdl;

import org.parboiled.Rule;
import org.parboiled.common.ImmutableList;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.util.CommandLine;
import ece351.vhdl.ast.Architecture;
import ece351.vhdl.ast.Component;
import ece351.vhdl.ast.Statement;
import ece351.vhdl.ast.DesignUnit;
import ece351.vhdl.ast.Entity;
import ece351.vhdl.ast.IfElseStatement;
import ece351.vhdl.ast.Process;
import ece351.vhdl.ast.VProgram;

//Parboiled requires that this class not be final
public/* final */class VParser extends VBase {

	public static void main(final String arg) {
		main(new String[] { arg });
	}

	public static void main(final String[] args) {
		final CommandLine c = new CommandLine(args);
		VProgram vprog = parse(c.readInputSpec());
		System.out.println(vprog);
	}

	public static VProgram parse(final String arg) {
		return (VProgram) process(VParser.class, arg).resultValue;
	}
    public Rule Program() {
    	return Sequence(push(new VProgram()),ZeroOrMore(DesignUnit(),swap(),push(((VProgram)pop()).append((DesignUnit)pop()))),EOI);
    }
    public Rule DesignUnit(){
    	return Sequence(EntityDecl(),ArchBody(),push(new DesignUnit(((Architecture)pop()),((Entity)pop()))));
    }
    public Rule EntityDecl(){
    	return Sequence(ENTITY(),W0(),id(),push(new Entity(match())),W0(),
    			IS(),W1(),PORT(),W0(),
    			'(',W0(),idList(),swap(),push(((Entity)pop()).setInput((ImmutableList<String>)pop())),
    					W0(),':',W0(),IN(),W0(),BIT(),"; ",
    			idList(),swap(),push(((Entity)pop()).setOutput((ImmutableList<String>)pop())),
    			W0(),": ",OUT(),W(),BIT(),W0(),')',W0(),';',
    			W0(),END(),W0(),
    			FirstOf(ENTITY(),id()),
    			W0(),"; ");
    }
    public Rule ArchBody(){
    	return Sequence(ARCHITECTURE(),W0(),id(),push(match()),W0(),OF(),W0(),id(),push(new Architecture(match(),(String)pop())),W0(),IS(),
    			W0(),
    			Optional(Sequence(SIGNAL(),W0(),idList(),swap(),
    					push(((Architecture)pop()).setSignals((ImmutableList<String>)pop())),
    					W0(),':',W0(),BIT(),W0(),';',W0())),
    			BEGIN(),W0(),
    			push(ImmutableList.of()),
    			ZeroOrMore(CompInst()),
    			swap(),push(((Architecture)pop()).setComponents(((ImmutableList<Component>)pop()))),
    			push(ImmutableList.of()),
    			OneOrMore(FirstOf(ProcessStmts(),SigAssnStmts())),
    			swap(),push(((Architecture)pop()).setStatements((ImmutableList<Statement>)pop())),
    			END(),W0(),id(),W0(),";",W0());
    }
    public Rule SigAssnStmts(){
    	return OneOrMore(SigAssnStmt());
    }
    public Rule SigAssnStmt(){
    	return Sequence(W0(),id(),push(new AssignmentStatement(new VarExpr(match()))),
    			W0(),"<=",W0(),
    			Expr(),
    			swap(),
    			push(((AssignmentStatement)pop()).setExpr((Expr)pop())),
    			swap(),
    			push(((ImmutableList<Statement>)pop()).append((Statement)pop())),
    			W0(),";",W0());
    }
    public Rule ProcessStmts(){
    	return OneOrMore(ProcessStmt());
    }
    public Rule ProcessStmt(){
    	return Sequence(PROCESS(),W0(),
    			'(',W0(),idList(),
    			W0(),')',W0(),
    			BEGIN(),W0(),
    			push(ImmutableList.<Statement>of()),
    			OneOrMore(FirstOf(IfElseStmts(),SigAssnStmts())),
    			W0(),ENDPROCESS(),W0(),"; ",
    					push(
		    			new Process(
		    					(ImmutableList<Statement>)pop(),(ImmutableList<String>)pop()
		    					)
		    			),swap(),
		    			push(
    					((ImmutableList<Statement>)pop()).append((Process)pop())
    					)
    			);
    }
    public Rule IfElseStmts(){
    	return OneOrMore(IfElseStmt());
    }
    public Rule IfElseStmt(){
    	return Sequence(
    			W0(),IF(),W0(),Expr(),W0(),THEN(),
    			push(ImmutableList.of()),
    			W0(),SigAssnStmts(),W0(),
    			ELSE(),W0(),
    			push(ImmutableList.of()),
    			SigAssnStmts(),W0(),
    			push(new IfElseStatement(
    					(ImmutableList<AssignmentStatement>)pop(),(ImmutableList<AssignmentStatement>)pop(),(Expr)pop())
    			),
    			swap(),push(
    					((ImmutableList<Statement>)pop()).append((IfElseStatement)pop())
    					),
    			ENDIF(),W0(),Optional(id()),W0(),';',W0());
    }
    public Rule CompInst(){
    	return Sequence(W0(),id(),push(match()),
    			W0(),':',W0(),ENTITY(),W0(),IgnoreCase("work."),id(),push(match()),
    			push(new Component((String)pop(),(String)pop())),
    			W0(),PORT(),W0(),MAP(),W0(),"( ",idList(),swap(),
    			push(((Component)pop()).setSignals((ImmutableList<String>)pop())),
    			swap(),push(
    					((ImmutableList<Component>)pop()).append((Component)pop())
    					),
    			W0(),") ",W0(),';',W0());
    }
    public Rule idList(){
    	return Sequence(id(),push(ImmutableList.of(match())),
    			ZeroOrMore(Sequence(W0(),',',W0(),
    			id(),push(
    					((ImmutableList<String>)pop()).append(match())
    					),W0())));
    }
    public Rule id() {
    	return Sequence(TestNot(Keyword()),Letter(),ZeroOrMore(FirstOf(Letter(),'_',CharRange('0','9'))));
    }
    public Rule Letter() {
    	return FirstOf(CharRange('A','Z'),CharRange('a','z'));
    }
    public Rule Expr() {
    	//return Sequence(W0(),Relation(),ZeroOrMore(Sequence(W0(),LogicOp(),W0(),Relation())));
    	return Sequence(W0(),XnorTerm(),ZeroOrMore(Sequence(W0(),XNOR,W0(),XnorTerm(),push(
				new XNOrExpr((Expr)pop(),(Expr)pop())))));
    }
    public Rule XnorTerm() {
    	return Sequence(W0(),XorTerm(),ZeroOrMore(Sequence(W0(),XOR(),W0(),XorTerm(),push(
				new XOrExpr((Expr)pop(),(Expr)pop())))));
    }
    public Rule XorTerm() {
    	return Sequence(W0(),NorTerm(),ZeroOrMore(Sequence(W0(),NOR(),W0(),NorTerm(),push(
				new NOrExpr((Expr)pop(),(Expr)pop())))));
    }
    public Rule NorTerm() {
    	return Sequence(W0(),NandTerm(),ZeroOrMore(Sequence(W0(),NAND(),W0(),NandTerm(),push(
				new NAndExpr((Expr)pop(),(Expr)pop())))));
    }
    public Rule NandTerm() {
    	return Sequence(W0(),OrTerm(),ZeroOrMore(Sequence(W0(),OR(),W0(),OrTerm(),push(
				new OrExpr((Expr)pop(),(Expr)pop()))
				)));
    }
    public Rule OrTerm() {
    	return Sequence(W0(),AndTerm(),ZeroOrMore(
    			Sequence(W0(),AND(),W0(),AndTerm(),push(
    					new AndExpr((Expr)pop(),(Expr)pop()))
    			)));
    }
    public Rule AndTerm() {
    	return Sequence(W0(),EqTerm(),Optional(Sequence(W0(),'=',W0(),EqTerm(),
    			swap(),
    			push(new EqualExpr((Expr)pop(),(Expr)pop())
    			)
    			)));
    }
    public Rule EqTerm() {
    	return FirstOf(Sequence(W0(),NOT(),W0(),EqTerm(),push(new NotExpr((Expr)pop()))),
    			Sequence(W0(),"( ",Expr(),W0(),") "),
    			Sequence(id(),push(new VarExpr(match()))),
    			Sequence(W0(),'\'',Constant(),push(ConstantExpr.make(match())),W0(),'\'',W0())
    			);
    }
    public Rule Constant() {
    	return FirstOf("0 ","1 ");
    }

}
