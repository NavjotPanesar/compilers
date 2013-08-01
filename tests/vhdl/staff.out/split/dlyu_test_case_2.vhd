entity DanWuzHere is port(
	A, B, C, D, E, F, G, H, I, J, K, L, S, T  : in bit;
	Z, Y, Z, O3, O4, O5  : out bit
);
end DanWuzHere;
architecture b of DanWuzHere is

begin
	process ( S, T, F, E  ) 
		begin
			if ( (( not ( S ) ) and ( not ( T ) )) ) then
				O3 <= F;
			else
				O3 <= E;
			end if;
		end process;
	process ( S, T, D, C  ) 
		begin
			if ( (( not ( S ) ) and ( not ( T ) )) ) then
				O4 <= D;
			else
				O4 <= C;
			end if;
		end process;
	process ( S, T, B, A  ) 
		begin
			if ( (( not ( S ) ) and ( not ( T ) )) ) then
				O5 <= B;
			else
				O5 <= A;
			end if;
		end process;
	process ( S, T, L, K  ) 
		begin
			if ( (( not ( ( not ( S ) ) ) ) and ( not ( ( not ( T ) ) ) )) ) then
				Z <= L;
			else
				Z <= K;
			end if;
		end process;
	process ( S, T, L, G  ) 
		begin
			if ( (( not ( ( not ( S ) ) ) ) and ( not ( ( not ( T ) ) ) )) ) then
				Z <= L;
			else
				Z <= G;
			end if;
		end process;
	process ( S, T, J, I  ) 
		begin
			if ( (( not ( ( not ( S ) ) ) ) and ( not ( ( not ( T ) ) ) )) ) then
				Y <= J;
			else
				Y <= I;
			end if;
		end process;
	process ( S, T, H, K  ) 
		begin
			if ( (( not ( ( not ( S ) ) ) ) and ( not ( ( not ( T ) ) ) )) ) then
				Z <= H;
			else
				Z <= K;
			end if;
		end process;
	process ( S, T, H, G  ) 
		begin
			if ( (( not ( ( not ( S ) ) ) ) and ( not ( ( not ( T ) ) ) )) ) then
				Z <= H;
			else
				Z <= G;
			end if;
		end process;
end b;
