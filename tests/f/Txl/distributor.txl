include "f.grm"
include "basic.txl"

rule main
	replace [program]
		p [program]
	construct out [program]
		p [absorber1][absorber2][absorber3][absorber4][complementor1][complementor2][clearparen][basiczero1][basiczero2][basiczero3][basiczero4][distributor1][distributor2]
	where not 
		p [= out]
	by
		out
end rule

rule distributor1
	replace [expression]
		e [expression]
	deconstruct e
		(r1 [relation] or r2 [relation]) and r3 [relation]
	by
		(r1 and r3) or (r2 and r3)
end rule

rule distributor2
	replace [expression]
		e [expression]
	deconstruct e
		r3 [relation] and  (r1 [relation] or r2 [relation])
	by
		(r3 and r1) or (r3 and r2)
end rule
