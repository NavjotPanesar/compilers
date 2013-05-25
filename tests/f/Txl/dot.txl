include "f.grm"

compounds
    ->
end compounds

redefine program
		...
	|	[id] [id] { [NL] [repeat dotExpression] }
	|	[empty]
end define

define dotExpression
		[IN] rankdir '= LR; [NL]
	|	node '[shape=plaintext']; [NL]
	|	[id] -> [id]; [NL]
end define

rule main
	replace $ [program]
		p [program]
	construct initialization [program]
		p[init]
	construct out [program]
		initialization [deleteProgram]
	where not 
		p [= out]
	by
		out [addDot]
end rule

rule deleteProgram
	replace [program]
		p [program]
	construct out [program]
		p [processOr][processAnd][clearparen][processAssignment]
	where not 
		p [= out]
	by
		out
end rule

function init
	replace $ [program]
		p [program]
	construct em [empty]
	construct re [repeat dotExpression]
		em
	construct orId [repeat id]
		orA orB orC orD orE orF orG orH orI orJ orK 
	construct andId [repeat id]
		andA andB andC andD andE andF andG andH andI andJ andK
	export orId
	export andId
	export re
	by
		p
end function

rule processOr
	replace [expression]
		e [expression]
	deconstruct e
		i1 [id] or i2 [id]
	construct x [id]
		'x
	construct i [id]
		x[processOrId]
	construct e1 [dotExpression]
		i1 -> i;
	construct e2 [dotExpression]
		i2 -> i;
	import re [repeat dotExpression]
	export re
		re[.e1][.e2]
	by
		i 
end rule

function processOrId
	replace $ [id]
		i [id]
	import orId [repeat id]
	deconstruct orId
		first [id] rest [repeat id]
	export orId
		rest
	by
		first
end function

rule clearparen
	replace [relation]
		r [relation]
	deconstruct r
		(i [id])
	by
		i
end rule

rule processAssignment
	replace [program]
		p [program]
	construct em [empty]
	deconstruct p
		i1[id] <= i2[id];
	construct e1 [dotExpression]
		i2 -> i1;
	import re [repeat dotExpression]
	export re
		re[.e1]
	by
		em
end rule

rule processAnd
	replace [expression]
		e [expression]
	deconstruct e
		i1 [id] and i2 [id]
	construct x [id]
		'x
	construct i [id]
		x[processAndId]
	construct e1 [dotExpression]
		i1 -> i;
	construct e2 [dotExpression]
		i2 -> i;
	import re [repeat dotExpression]
	export re
		re[.e1][.e2]
	by
		i 
end rule

function processAndId
	replace $ [id]
		i [id]
	import andId [repeat id]
	deconstruct andId
		first [id] rest [repeat id]
	export andId
		rest
	by
		first
end function

function addDot
	replace [program]
		p [program]
	import re [repeat dotExpression]
	by
		digraph g {
		rankdir '= LR;
		node '[shape=plaintext'];
		re
		}
end function
