entity M is port(
	A, B, C, D, S  : in bit;
	X, Y  : out bit
);
end M;
architecture beh of M is

begin
	process ( S, C, D  ) 
		begin
			if ( ( not ( ( not ( S ) ) ) ) ) then
				X <= C;
			else
				X <= D;
			end if;
		end process;
	process ( S, A, B  ) 
		begin
			if ( ( not ( ( not ( S ) ) ) ) ) then
				Y <= A;
			else
				Y <= B;
			end if;
		end process;
end beh;
