package ece351.vhdl;

import org.parboiled.Rule;

import ece351.util.CommandLine;

//Parboiled requires that this class not be final
public /*final*/ class VRecognizer extends VBase {

	public static void main(final String arg) {
		main(new String[]{arg});
	}
	
	public static void main(final String[] args) {
		final CommandLine c = new CommandLine(args);
    	process(VRecognizer.class, c.readInputSpec());
    }

    public Rule Program() {
    	// TODO: Write a recognizer that accepts grammatical VHDL inputs
    	// For the grammar production Id, ensure that the Id does not match any of the keywords specified
    	// in the rule, 'Keyword'
    	return Sequence(ZeroOrMore(DesignUnit()),EOI);
    }
    public Rule DesignUnit(){
    	return Sequence(EntityDecl(),ArchBody());
    }
    public Rule EntityDecl(){
    	return Sequence(ENTITY(),W0(),id(),W0(),
    			IS(),W1(),PORT(),W0(),
    			'(',W0(),idList(),W0(),':',W0(),IN(),W0(),BIT(),"; ",
    			idList(),W0(),": ",OUT(),W(),BIT(),W0(),')',W0(),';',
    			W0(),END(),W0(),
    			FirstOf(ENTITY(),id()),
    			W0(),"; ");
    }
    public Rule ArchBody(){
    	return Sequence(ARCHITECTURE(),W0(),id(),W0(),OF(),W0(),id(),W0(),IS(),
    			W0(),
    			Optional(Sequence(SIGNAL(),W0(),idList(),W0(),':',W0(),BIT(),W0(),';',W0())),
    			BEGIN(),W0(),ZeroOrMore(CompInst()),OneOrMore(FirstOf(ProcessStmts(),SigAssnStmts())),
    			END(),W0(),id(),W0(),";",W0());
    }
    public Rule SigAssnStmts(){
    	return OneOrMore(SigAssnStmt());
    }
    public Rule SigAssnStmt(){
    	return Sequence(W0(),id(),W0(),"<=",W0(),Expr(),W0(),";",W0());
    }
    public Rule ProcessStmts(){
    	return OneOrMore(ProcessStmt());
    }
    public Rule ProcessStmt(){
    	return Sequence(PROCESS(),W0(),
    			'(',W0(),idList(),W0(),')',W0(),
    			BEGIN(),W0(),
    			OneOrMore(FirstOf(IfElseStmts(),SigAssnStmts())),
    			W0(),ENDPROCESS(),W0(),"; "
    			);
    }
    public Rule IfElseStmts(){
    	return OneOrMore(IfElseStmt());
    }
    public Rule IfElseStmt(){
    	return Sequence(
    			W0(),IF(),W0(),Expr(),W0(),THEN(),
    			W0(),SigAssnStmts(),W0(),
    			ELSE(),W0(),
    			SigAssnStmts(),W0(),
    			ENDIF(),W0(),Optional(id()),W0(),';',W0());
    }
    public Rule CompInst(){
    	return Sequence(W0(),id(),W0(),':',W0(),ENTITY(),W0(),IgnoreCase("work."),id(),W0(),PORT(),W0(),MAP(),W0(),"( ",idList(),W0(),") ",W0(),';',W0());
    }
    public Rule idList(){
    	return Sequence(id(),ZeroOrMore(Sequence(W0(),',',W0(),id(),W0())));
    }
    public Rule id() {
    	return Sequence(TestNot(Keyword()),Letter(),ZeroOrMore(FirstOf(Letter(),'_',CharRange('0','9'))));
    }
    public Rule Letter() {
    	return FirstOf(CharRange('A','Z'),CharRange('a','z'));
    }
    public Rule Expr() {
    	//return Sequence(W0(),Relation(),ZeroOrMore(Sequence(W0(),LogicOp(),W0(),Relation())));
    	return Sequence(W0(),XnorTerm(),ZeroOrMore(Sequence(W0(),XNOR,W0(),XnorTerm())));
    }
    public Rule XnorTerm() {
    	return Sequence(W0(),XorTerm(),ZeroOrMore(Sequence(W0(),XOR(),W0(),XorTerm())));
    }
    public Rule XorTerm() {
    	return Sequence(W0(),NorTerm(),ZeroOrMore(Sequence(W0(),NOR(),W0(),NorTerm())));
    }
    public Rule NorTerm() {
    	return Sequence(W0(),NandTerm(),ZeroOrMore(Sequence(W0(),NAND(),W0(),NandTerm())));
    }
    public Rule NandTerm() {
    	return Sequence(W0(),OrTerm(),ZeroOrMore(Sequence(W0(),OR(),W0(),OrTerm())));
    }
    public Rule OrTerm() {
    	return Sequence(W0(),AndTerm(),ZeroOrMore(Sequence(W0(),AND(),W0(),AndTerm())));
    }
    public Rule AndTerm() {
    	return Sequence(W0(),EqTerm(),Optional(Sequence(W0(),'=',W0(),EqTerm())));
    }
    public Rule EqTerm() {
    	return FirstOf(Sequence(W0(),NOT(),W0(),EqTerm()),Sequence(W0(),"( ",Expr(),W0(),") "),id(),Sequence(W0(),'\'',Constant(),W0(),'\'',W0()));
    }
    public Rule Constant() {
    	return FirstOf("0 ","1 ");
    }
    /*
    public Rule Relation(){
    	return Sequence(Factor(),Optional(Sequence(W0(),RelOp(),W0(),Factor())));
    }
    public Rule LogicOp(){
    	return FirstOf(AND,OR,XOR,NAND,NOR,XNOR);
    }
    public Rule RelOp(){
    	return Ch('=');
    }
    public Rule Factor(){
    	return Sequence(Optional(NOT),W0(),Literal());
    }
    public Rule Literal(){
    	return FirstOf(id(),Sequence('\'',W0(),Constant(),W0(),'\''),Sequence('(',W0(),Expr(),W0(),')'));
    }*/
}
