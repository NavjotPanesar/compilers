rule absorber1
	replace [expression]
		e [expression]
	deconstruct e
		r1 [relation] and ( r2 [relation] or r3 [relation] )
	where
		r1 [= r2]
	by
		r1
end rule

rule absorber2
	replace [expression]
		e [expression]
	deconstruct e
		r1 [relation] and ( r3 [relation] or r2 [relation] )
	where
		r1 [= r2]
	by
		r1
end rule

rule absorber3
	replace [expression]
		e [expression]
	deconstruct e
		( r3 [relation] or r2 [relation] ) and r1 [relation]
	where
		r1 [= r2]
	by
		r1
end rule

rule absorber4
	replace [expression]
		e [expression]
	deconstruct e
		( r2 [relation] or r3 [relation] ) and r1 [relation]
	where
		r1 [= r2]
	by
		r1
end rule