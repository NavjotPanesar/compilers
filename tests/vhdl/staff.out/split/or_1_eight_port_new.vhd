entity ORR_gate is port(
    xx, yy  : in bit;
    zz  : out bit
);
end ORR_gate;
architecture ORR_gate_arch of ORR_gate is

begin
    process ( yy, xx  ) 
        begin
            if ( ( ( not ( ( ( yy and ( not ( '0' ) ) ) or ( ( not ( yy ) ) and '0' ) ) ) ) and ( not ( ( ( xx and ( not ( '0' ) ) ) or ( ( not ( xx ) ) and '0' ) ) ) ) ) ) then
                zz <= '0';
            else
                zz <= '1';
            end if;
        end process;
end ORR_gate_arch;

entity four_port_ORR_gate is port(
    x, y, z, zz  : in bit;
    r  : out bit
);
end four_port_ORR_gate;
architecture four_port_structure of four_port_ORR_gate is
    signal m, n  : bit;
begin
    process ( m, n  ) 
        begin
            r <= ( m or n );
        end process;
    process ( zz, z  ) 
        begin
            if ( ( ( not ( ( ( zz and ( not ( '0' ) ) ) or ( ( not ( zz ) ) and '0' ) ) ) ) and ( not ( ( ( z and ( not ( '0' ) ) ) or ( ( not ( z ) ) and '0' ) ) ) ) ) ) then
                n <= '0';
            else
                n <= '1';
            end if;
        end process;
    process ( y, x  ) 
        begin
            if ( ( ( not ( ( ( y and ( not ( '0' ) ) ) or ( ( not ( y ) ) and '0' ) ) ) ) and ( not ( ( ( x and ( not ( '0' ) ) ) or ( ( not ( x ) ) and '0' ) ) ) ) ) ) then
                m <= '0';
            else
                m <= '1';
            end if;
        end process;
end four_port_structure;

entity eight_port_ORR_gate is port(
    a, b, c, d, e, f, g, h  : in bit;
    y  : out bit
);
end eight_port_ORR_gate;
architecture eight_port_structure of eight_port_ORR_gate is
    signal r1, r2, comp3_m, comp3_n, comp4_m, comp4_n  : bit;
begin
    process ( r1, r2  ) 
        begin
            y <= ( r1 or r2 );
        end process;
    process ( comp3_m, comp3_n  ) 
        begin
            r2 <= ( comp3_m or comp3_n );
        end process;
    process ( h, g  ) 
        begin
            if ( ( ( not ( ( ( h and ( not ( '0' ) ) ) or ( ( not ( h ) ) and '0' ) ) ) ) and ( not ( ( ( g and ( not ( '0' ) ) ) or ( ( not ( g ) ) and '0' ) ) ) ) ) ) then
                comp3_n <= '0';
            else
                comp3_n <= '1';
            end if;
        end process;
    process ( f, e  ) 
        begin
            if ( ( ( not ( ( ( f and ( not ( '0' ) ) ) or ( ( not ( f ) ) and '0' ) ) ) ) and ( not ( ( ( e and ( not ( '0' ) ) ) or ( ( not ( e ) ) and '0' ) ) ) ) ) ) then
                comp3_m <= '0';
            else
                comp3_m <= '1';
            end if;
        end process;
    process ( comp4_m, comp4_n  ) 
        begin
            r1 <= ( comp4_m or comp4_n );
        end process;
    process ( d, c  ) 
        begin
            if ( ( ( not ( ( ( d and ( not ( '0' ) ) ) or ( ( not ( d ) ) and '0' ) ) ) ) and ( not ( ( ( c and ( not ( '0' ) ) ) or ( ( not ( c ) ) and '0' ) ) ) ) ) ) then
                comp4_n <= '0';
            else
                comp4_n <= '1';
            end if;
        end process;
    process ( b, a  ) 
        begin
            if ( ( ( not ( ( ( b and ( not ( '0' ) ) ) or ( ( not ( b ) ) and '0' ) ) ) ) and ( not ( ( ( a and ( not ( '0' ) ) ) or ( ( not ( a ) ) and '0' ) ) ) ) ) ) then
                comp4_m <= '0';
            else
                comp4_m <= '1';
            end if;
        end process;
end eight_port_structure;