entity full_adder is port (
    a, b, c: in bit;
    s, o: out bit
);
end full_adder;

architecture full_adder_arch of full_adder is 
begin  
    o <= (a and b) or ( c and (a xor b));
    s <= c xor (a xor b);
end full_adder_arch;

