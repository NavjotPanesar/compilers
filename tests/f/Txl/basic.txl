include "absorber.txl"
include "complementor.txl"

rule clearparen
	replace [relation]
		r [relation]
	deconstruct r
		(n [number])
	by
		n
end rule

rule basiczero1
	replace [expression]
		e [expression]
	deconstruct e
		r1 [relation] l [logical_operator] r2 [relation]
	deconstruct r1
		n [number]
	deconstruct l
		'and
	where n [= 0]
	by
		0
end rule

rule basiczero2
	replace [expression]
		e [expression]
	deconstruct e
		r1 [relation] l [logical_operator] r2 [relation]
	deconstruct r2
		n [number]
	deconstruct l
		'and
	where n [= 0]
	by
		0
end rule

rule basiczero3
	replace [expression]
		e [expression]
	deconstruct e
		r1 [relation] l [logical_operator] r2 [relation]
	deconstruct r1
		n [number]
	deconstruct l
		'or
	where n [= 0]
	by
		r2
end rule

rule basiczero4
	replace [expression]
		e [expression]
	deconstruct e
		r1 [relation] l [logical_operator] r2 [relation]
	deconstruct r2
		n [number]
	deconstruct l
		'or
	where n [= 0]
	by
		r1
end rule

rule basicone1
	replace [expression]
		e [expression]
	deconstruct e
		r1 [relation] l [logical_operator] r2 [relation]
	deconstruct r1
		n [number]
	deconstruct l
		'or
	where n [= 1]
	by
		1
end rule

rule basicone2
	replace [expression]
		e [expression]
	deconstruct e
		r1 [relation] l [logical_operator] r2 [relation]
	deconstruct r2
		n [number]
	deconstruct l
		'or
	where n [= 1]
	by
		1
end rule

rule basicone3
	replace [expression]
		e [expression]
	deconstruct e
		r1 [relation] l [logical_operator] r2 [relation]
	deconstruct r1
		n [number]
	deconstruct l
		'and
	where n [= 1]
	by
		r2
end rule

rule basicone4
	replace [expression]
		e [expression]
	deconstruct e
		r1 [relation] l [logical_operator] r2 [relation]
	deconstruct r2
		n [number]
	deconstruct l
		'and
	where n [= 1]
	by
		r2
end rule