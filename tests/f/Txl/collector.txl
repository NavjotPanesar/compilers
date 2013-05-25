include "f.grm"
include "basic.txl"

rule main
	replace [program]
		p [program]
	construct out [program]
		p [collector1][collector2][collector3][collector4][complementor1][complementor2][clearparen][basiczero1][basiczero2][basiczero3][basiczero4][absorber1][absorber2][absorber3][absorber4]
	where not 
		p [= out]
	by
		out
end rule

rule collector1
	replace [expression]
		e [expression]
	deconstruct e
		(r1 [relation] and r3 [relation]) or (r2 [relation] and r4 [relation])
	where
		r3 [= r4]
	by
		(r1 or r2) and r3 
end rule

rule collector2
	replace [expression]
		e [expression]
	deconstruct e
		(r1 [relation] and r3 [relation]) or (r4 [relation] and r2 [relation])
	where
		r3 [= r4]
	by
		(r1 or r2) and r3
end rule

rule collector3
	replace [expression]
		e [expression]
	deconstruct e
		(r3 [relation] and r1 [relation]) or (r4 [relation] and r2 [relation])
	where
		r3 [= r4]
	by
		r3 and (r1 or r2)
end rule

rule collector4
	replace [expression]
		e [expression]
	deconstruct e
		(r3 [relation] and r1 [relation]) or (r2 [relation] and r4 [relation])
	where
		r3 [= r4]
	by
		r3 and (r1 or r2)
end rule
