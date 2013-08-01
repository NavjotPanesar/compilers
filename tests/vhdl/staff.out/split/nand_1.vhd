entity NAND_gate is port(
	x, y  : in bit;
	F  : out bit
);
end NAND_gate;
architecture NAND_gate_arch of NAND_gate is

begin
	process ( x, y  ) 
		begin
			if ( (( not ( ( not ( x ) ) ) ) and ( not ( ( not ( y ) ) ) )) ) then
				F <= '0';
			else
				F <= '1';
			end if;
		end process;
end NAND_gate_arch;
