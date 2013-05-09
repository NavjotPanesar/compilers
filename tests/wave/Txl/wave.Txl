#pragma -raw

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% This program renders square waves in SVG (Scalable Vector
%%% Graphics, an XML file format). The input is named sequences of
%%% ones and zeroes and the output is a graphical rendering of those
%%% sequences.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Input Grammar, files of the form:
%%%     A:  U 0 1 0 1 0 1
%%%     B:  U 1 0 1 0 1 0
%%%     OR: U 1 1 1 1 1 1
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

define program
    [repeat pin]
end define

define pin
    [id] ': [repeat segment] 
end define

define segment
    'U | [number]
end define


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Output Grammar, a subset of SVG
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

redefine program
    [opt header] [NL] [repeat pin] [NL] [opt footer]
end define

define header
    <?xml version="1.0" [SP] encoding="UTF-8"?> [NL]
    <!DOCTYPE svg PUBLIC [SP]"-//W3C//DTD SVG 1.1//EN" [SP] "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"> [NL]
    <svg xmlns="http://www.w3.org/2000/svg"[SP] width="100%"[SP] height="100%"[SP] version="1.1"> [NL]
    <style type="text/css" > <'!'[CDATA'[ 
        'line '{ stroke: #006600; fill: #00cc00; '}  [SP]
        'text '{ font-size:"large"; font-family:"sans-serif" '} ']']> 
    </style> [NL]
end define

define footer
    </svg>
end define

redefine pin
    ...
  | [NL][IN] [repeat segment] [EX][NL]
end define

redefine segment
    ...
  | [opt label] [repeat line]
end define

define label
    <text x=[ns][SP] y=[ns]> [id] </text> [NL]
end define

define line
    <'line x1=[ns][SP] x2=[ns][SP] y1=[ns][SP] y2=[ns][SP] /> [NL]
end define

define ns
    [number] | [stringlit]
end define


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Transformation
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

rule main
    export YOffset [number]
        0
    replace [program]
        P [program]
    construct P2 [program]
        P [insertHeader][renderPin][n2s]
    where not
        P2 [= P]
    by 
        P2 
end rule

rule insertHeader
    replace [program]
        Pins [repeat pin]
    by
        <?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
        <svg xmlns="http://www.w3.org/2000/svg" width="100%" height="100%" version="1.1">
        <style type="text/css" > <'!'[CDATA'[ 
            'line '{ stroke: #006600; fill: #00cc00; '} 
            'text '{ font-size:"large"; font-family:"sans-serif" '} ']']> 
        </style> 
        Pins
        </svg>
end rule

rule renderPin
    replace [pin]
        I [id] ': W [repeat segment] 
    import YOffset [number]
    export YOffset 
        YOffset [+ 200]
    export X [number]
        0 % reset the X for each pin
    by 
        <text x=X [+ 50] y=YOffset [- 50]> I </text>
        W[renderSegmentU][renderSegment]
end rule

rule renderSegmentU
    import YOffset [number]
    export Y [number]
        YOffset [- 50]
    replace [segment]
        'U
    by
        0.50
end rule

rule renderSegment
    import X [number]
    import Y [number]
    import YOffset [number]
    replace [segment]
        N [number]
    export X 
        X [+ 100]
    construct OldY [number]
        Y
    construct BigN [number]
        N [* 100]
    export Y 
        YOffset [- BigN]
    by
        % vertical line
        <'line x1=X x2=X y1=OldY y2=Y />
        % horizontal line
        <'line x1=X x2=X [+ 100] y1=Y y2=Y /> 
end rule

% This rule is just here for technical reasons. TXL requires us to
% store the number as number in order to perform arithmetic on them,
% but to output them we want to convert them to strings.
rule n2s
    replace [ns]
        N [number]
    construct S [stringlit]
        _ [quote N]
    by
        S
end rule

