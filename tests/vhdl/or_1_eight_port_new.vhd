ENTITY ORR_gate is port (
    xx,yy : in bit;
    zz: out bit
);
end ORR_gate;

architecture ORR_gate_arch of ORR_gate is begin
    process(xx,yy)
    begin
        if ((yy='0') and (xx='0')) then
            zz <= '0';
        else
            zz <= '1';
        end if;
    end process;
end ORR_gate_arch;

entity four_port_ORR_gate is port (
    x, y, z, zz : in bit;
    r : out bit
);
end four_port_ORR_gate;

architecture four_port_structure of four_port_ORR_gate is
    signal m, n : bit;
begin
    OR2: entity work.ORR_gate port map(z,zz,n);
    OR1: entity work.ORR_gate port map(x,y,m);
    process(m,n)
    begin
        r <= m or n;
    end process;
end four_port_structure;

entity eight_port_ORR_gate is port (
    a,b,c,d,e,f,g,h : in bit;
    y : out bit
);
end eight_port_OR_gate;

architecture eight_port_structure of eight_port_ORR_gate is
    signal r1, r2 : bit;
begin
    OR2: entity work.four_port_ORR_gate port map(e,f,g,h,r2);
    OR1: entity work.four_port_ORR_gate port map(a,b,c,d,r1);
    process(r1,r2)
    begin
        y <= r1 or r2;
    end process;
end eight_port_structure;
