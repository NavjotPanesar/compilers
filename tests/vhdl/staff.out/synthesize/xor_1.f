condition1 <= ( ( ( not XOR_gatex ) and ( not XOR_gatey ) ) or ( XOR_gatex and XOR_gatey ) );
XOR_gateF <= ( condition1 and ( '0' ) ) or ( ( not condition1 ) and ( '1' ) );
