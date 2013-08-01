entity ORR_gate is port(
    xx, yy  : in bit;
    zz  : out bit
);
end ORR_gate;
architecture ORR_gate_arch of ORR_gate is

begin
    process ( xx, yy  ) 
        begin
            if ( ( ( not ( ( ( '0' and ( not ( yy ) ) ) or ( ( not ( '0' ) ) and yy ) ) ) ) and ( not ( ( ( '0' and ( not ( xx ) ) ) or ( ( not ( '0' ) ) and xx ) ) ) ) ) ) then
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
    process ( z, zz  ) 
        begin
            if ( ( ( not ( ( ( '0' and ( not ( zz ) ) ) or ( ( not ( '0' ) ) and zz ) ) ) ) and ( not ( ( ( '0' and ( not ( z ) ) ) or ( ( not ( '0' ) ) and z ) ) ) ) ) ) then
                n <= '0';
            else
                n <= '1';
            end if;
        end process;
    process ( x, y  ) 
        begin
            if ( ( ( not ( ( ( '0' and ( not ( y ) ) ) or ( ( not ( '0' ) ) and y ) ) ) ) and ( not ( ( ( '0' and ( not ( x ) ) ) or ( ( not ( '0' ) ) and x ) ) ) ) ) ) then
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
    process ( g, h  ) 
        begin
            if ( ( ( not ( ( ( '0' and ( not ( h ) ) ) or ( ( not ( '0' ) ) and h ) ) ) ) and ( not ( ( ( '0' and ( not ( g ) ) ) or ( ( not ( '0' ) ) and g ) ) ) ) ) ) then
                comp3_n <= '0';
            else
                comp3_n <= '1';
            end if;
        end process;
    process ( e, f  ) 
        begin
            if ( ( ( not ( ( ( '0' and ( not ( f ) ) ) or ( ( not ( '0' ) ) and f ) ) ) ) and ( not ( ( ( '0' and ( not ( e ) ) ) or ( ( not ( '0' ) ) and e ) ) ) ) ) ) then
                comp3_m <= '0';
            else
                comp3_m <= '1';
            end if;
        end process;
    process ( comp4_m, comp4_n  ) 
        begin
            r1 <= ( comp4_m or comp4_n );
        end process;
    process ( c, d  ) 
        begin
            if ( ( ( not ( ( ( '0' and ( not ( d ) ) ) or ( ( not ( '0' ) ) and d ) ) ) ) and ( not ( ( ( '0' and ( not ( c ) ) ) or ( ( not ( '0' ) ) and c ) ) ) ) ) ) then
                comp4_n <= '0';
            else
                comp4_n <= '1';
            end if;
        end process;
    process ( a, b  ) 
        begin
            if ( ( ( not ( ( ( '0' and ( not ( b ) ) ) or ( ( not ( '0' ) ) and b ) ) ) ) and ( not ( ( ( '0' and ( not ( a ) ) ) or ( ( not ( '0' ) ) and a ) ) ) ) ) ) then
                comp4_m <= '0';
            else
                comp4_m <= '1';
            end if;
        end process;
end eight_port_structure;