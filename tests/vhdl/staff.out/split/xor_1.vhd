entity XOR_gate is port(
	x, y  : in bit;
	F  : out bit
);
end XOR_gate;
architecture XOR_gate_arch of XOR_gate is

begin
	process ( x, y  ) 
		begin
			if ( ((( not ( ( not ( x ) ) ) ) and ( not ( ( not ( y ) ) ) )) or (( not ( x ) ) and ( not ( y ) ))) ) then
				F <= '0';
			else
				F <= '1';
			end if;
		end process;
end XOR_gate_arch;
