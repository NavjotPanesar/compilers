rule complementor1
	replace [expression]
		e [expression]
	deconstruct e
		r1 [relation] and r2 [relation]
	deconstruct r2
		(n [prefixnot] e1 [expression])
	deconstruct e1
		r3 [relation]
	where
		r1 [= r3]
	by
		0
end rule

rule complementor2
	replace [expression]
		e [expression]
	deconstruct e
		r2[relation] and r1 [relation]
	deconstruct r2
		(n [prefixnot] e1 [expression])
	deconstruct e1
		r3 [relation]
	where
		r1 [= r3]
	by
		0
end rule
