condition1 <= ( NAND_gatex and NAND_gatey );
NAND_gateF <= ( condition1 and ( '0' ) ) or ( ( not condition1 ) and ( '1' ) );
