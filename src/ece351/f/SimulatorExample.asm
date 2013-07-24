.386
.model flat,stdcall
option casemap:none

Java_Simulator_1opt1_1or_1true1_Evalx PROTO :DWORD, :DWORD, :BYTE

.code
LibMain proc hInstance:DWORD, reason:DWORD, reserved1:DWORD
    mov eax, 1
    ret
LibMain endp

Java_Simulator_1opt1_1or_1true1_Evalx proc JNIEnv:DWORD, jobject:DWORD, in_a:BYTE
    ;  (a, true)or

    mov EAX, 0
    mov EBX, 0
    mov AL,in_a
    mov EBX,1
    or EAX, EBX
    ret
Java_Simulator_1opt1_1or_1true1_Evalx endp
End LibMain
