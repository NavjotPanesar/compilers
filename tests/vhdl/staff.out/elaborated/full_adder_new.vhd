entity full_adder is port(
    a, b, c  : in bit;
    s, o  : out bit
);
end full_adder;
architecture full_adder_arch of full_adder is

begin
    o <= ( ( a and b ) or ( c and ( ( a and ( not ( b ) ) ) or ( ( not ( a ) ) and b ) ) ) );
    s <= ( ( c and ( not ( ( ( a and ( not ( b ) ) ) or ( ( not ( a ) ) and b ) ) ) ) ) or ( ( not ( c ) ) and ( ( a and ( not ( b ) ) ) or ( ( not ( a ) ) and b ) ) ) );
end full_adder_arch;
