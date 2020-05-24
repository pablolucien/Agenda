             member('pf16')

UpdRawSten Procedure
index short
index_1 short
            ! Tablas de respuestas de cada factor
Answer_A    group, dim(10), pre(A)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_B    group, dim(13), pre(B)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_C    group, dim(13), pre(C)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_E    group, dim(13), pre(E)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_F    group, dim(13), pre(F)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_G    group, dim(10), pre(G)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_H    group, dim(13), pre(H)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_I    group, dim(10), pre(I)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_L    group, dim(10), pre(L)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_M    group, dim(13), pre(M)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_N    group, dim(10), pre(N)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_O    group, dim(13), pre(O)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_Q1   group, dim(13), pre(Q1)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_Q2   group, dim(13), pre(Q2)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_Q3   group, dim(13), pre(Q3)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .
Answer_Q4   group, dim(13), pre(Q4)
Preg          byte           ! Preguntas asociadas a los grupos
Resp          byte, dim(3)   ! Valores de las respuestas A, B, C
            .

  code

  ! Tablas de las respuestas correspondientes a cada factor
  ! y el valor de 'a', 'b' o 'c'
  ! Factor A
  A:Preg[1]  =   3; A:Resp[1,1]  = 2; A:Resp[1,2]  = 1; A:Resp[1,3]  = 0
  A:Preg[2]  =  26; A:Resp[2,1]  = 0; A:Resp[2,2]  = 1; A:Resp[2,3]  = 2
  A:Preg[3]  =  27; A:Resp[3,1]  = 0; A:Resp[3,2]  = 1; A:Resp[3,3]  = 2
  A:Preg[4]  =  51; A:Resp[4,1]  = 0; A:Resp[4,2]  = 1; A:Resp[4,3]  = 2
  A:Preg[5]  =  52; A:Resp[5,1]  = 2; A:Resp[5,2]  = 1; A:Resp[5,3]  = 0
  A:Preg[6]  =  76; A:Resp[6,1]  = 0; A:Resp[6,2]  = 1; A:Resp[6,3]  = 2
  A:Preg[7]  = 101; A:Resp[7,1]  = 2; A:Resp[7,2]  = 1; A:Resp[7,3]  = 0
  A:Preg[8]  = 126; A:Resp[8,1]  = 2; A:Resp[8,2]  = 1; A:Resp[8,3]  = 0
  A:Preg[9]  = 151; A:Resp[9,1]  = 0; A:Resp[9,2]  = 1; A:Resp[9,3]  = 2
  A:Preg[10] = 176; A:Resp[10,1] = 2; A:Resp[10,2] = 1; A:Resp[10,3] = 0

  ! Factor B
  B:Preg[1]  =  28; B:Resp[1,1]  = 0; B:Resp[1,2]  = 1; B:Resp[1,3]  = 0
  B:Preg[2]  =  53; B:Resp[2,1]  = 0; B:Resp[2,2]  = 1; B:Resp[2,3]  = 0
  B:Preg[3]  =  54; B:Resp[3,1]  = 0; B:Resp[3,2]  = 1; B:Resp[3,3]  = 0
  B:Preg[4]  =  77; B:Resp[4,1]  = 0; B:Resp[4,2]  = 0; B:Resp[4,3]  = 1
  B:Preg[5]  =  78; B:Resp[5,1]  = 0; B:Resp[5,2]  = 1; B:Resp[5,3]  = 0
  B:Preg[6]  = 102; B:Resp[6,1]  = 0; B:Resp[6,2]  = 0; B:Resp[6,3]  = 1
  B:Preg[7]  = 103; B:Resp[7,1]  = 0; B:Resp[7,2]  = 1; B:Resp[7,3]  = 0
  B:Preg[8]  = 127; B:Resp[8,1]  = 0; B:Resp[8,2]  = 0; B:Resp[8,3]  = 1
  B:Preg[9]  = 128; B:Resp[9,1]  = 0; B:Resp[9,2]  = 1; B:Resp[9,3]  = 0
  B:Preg[10] = 152; B:Resp[10,1] = 1; B:Resp[10,2] = 0; B:Resp[10,3] = 0
  B:Preg[11] = 153; B:Resp[11,1] = 0; B:Resp[11,2] = 0; B:Resp[11,3] = 1
  B:Preg[12] = 177; B:Resp[12,1] = 1; B:Resp[12,2] = 0; B:Resp[12,3] = 0
  B:Preg[13] = 178; B:Resp[13,1] = 1; B:Resp[13,2] = 0; B:Resp[13,3] = 0

  ! Factor C
  C:Preg[1]  =   4; C:Resp[1,1]  = 2; C:Resp[1,2]  = 1; C:Resp[1,3]  = 0
  C:Preg[2]  =   5; C:Resp[2,1]  = 0; C:Resp[2,2]  = 1; C:Resp[2,3]  = 2
  C:Preg[3]  =  29; C:Resp[3,1]  = 0; C:Resp[3,2]  = 1; C:Resp[3,3]  = 2
  C:Preg[4]  =  30; C:Resp[4,1]  = 2; C:Resp[4,2]  = 1; C:Resp[4,3]  = 0
  C:Preg[5]  =  55; C:Resp[5,1]  = 2; C:Resp[5,2]  = 1; C:Resp[5,3]  = 0
  C:Preg[6]  =  79; C:Resp[6,1]  = 0; C:Resp[6,2]  = 1; C:Resp[6,3]  = 2
  C:Preg[7]  =  80; C:Resp[7,1]  = 0; C:Resp[7,2]  = 1; C:Resp[7,3]  = 2
  C:Preg[8]  = 104; C:Resp[8,1]  = 2; C:Resp[8,2]  = 1; C:Resp[8,3]  = 0
  C:Preg[9]  = 105; C:Resp[9,1]  = 2; C:Resp[9,2]  = 1; C:Resp[9,3]  = 0
  C:Preg[10] = 129; C:Resp[10,1] = 0; C:Resp[10,2] = 1; C:Resp[10,3] = 2
  C:Preg[11] = 130; C:Resp[11,1] = 2; C:Resp[11,2] = 1; C:Resp[11,3] = 0
  C:Preg[12] = 154; C:Resp[12,1] = 0; C:Resp[12,2] = 1; C:Resp[12,3] = 2
  C:Preg[13] = 179; C:Resp[13,1] = 2; C:Resp[13,2] = 1; C:Resp[13,3] = 0

  ! Factor E
  E:Preg[1]  =   6; E:Resp[1,1]  = 0; E:Resp[1,2] = 1; E:Resp[1,3]   = 2
  E:Preg[2]  =   7; E:Resp[2,1]  = 2; E:Resp[2,2]  = 1; E:Resp[2,3]  = 0
  E:Preg[3]  =  31; E:Resp[3,1]  = 0; E:Resp[3,2]  = 1; E:Resp[3,3]  = 2
  E:Preg[4]  =  32; E:Resp[4,1]  = 0; E:Resp[4,2]  = 1; E:Resp[4,3]  = 2
  E:Preg[5]  =  56; E:Resp[5,1]  = 2; E:Resp[5,2]  = 1; E:Resp[5,3]  = 0
  E:Preg[6]  =  57; E:Resp[6,1]  = 0; E:Resp[6,2]  = 1; E:Resp[6,3]  = 2
  E:Preg[7]  =  81; E:Resp[7,1]  = 0; E:Resp[7,2]  = 1; E:Resp[7,3]  = 2
  E:Preg[8]  = 106; E:Resp[8,1]  = 0; E:Resp[8,2]  = 1; E:Resp[8,3]  = 2
  E:Preg[9]  = 131; E:Resp[9,1]  = 2; E:Resp[9,2]  = 1; E:Resp[9,3]  = 0
  E:Preg[10] = 155; E:Resp[10,1] = 2; E:Resp[10,2] = 1; E:Resp[10,3] = 0
  E:Preg[11] = 156; E:Resp[11,1] = 2; E:Resp[11,2] = 1; E:Resp[11,3] = 0
  E:Preg[12] = 180; E:Resp[12,1] = 2; E:Resp[12,2] = 1; E:Resp[12,3] = 0
  E:Preg[13] = 181; E:Resp[13,1] = 2; E:Resp[13,2] = 1; E:Resp[13,3] = 0

  ! Factor F
  F:Preg[1]  =   8; F:Resp[1,1]  = 0; F:Resp[1,2]  = 1; F:Resp[1,3]  = 2
  F:Preg[2]  =  33; F:Resp[2,1]  = 2; F:Resp[2,2]  = 1; F:Resp[2,3]  = 0
  F:Preg[3]  =  58; F:Resp[3,1]  = 2; F:Resp[3,2]  = 1; F:Resp[3,3]  = 0
  F:Preg[4]  =  82; F:Resp[4,1]  = 0; F:Resp[4,2]  = 1; F:Resp[4,3]  = 2
  F:Preg[5]  =  83; F:Resp[5,1]  = 2; F:Resp[5,2]  = 1; F:Resp[5,3]  = 0
  F:Preg[6]  = 107; F:Resp[6,1]  = 0; F:Resp[6,2]  = 1; F:Resp[6,3]  = 2
  F:Preg[7]  = 108; F:Resp[7,1]  = 0; F:Resp[7,2]  = 1; F:Resp[7,3]  = 2
  F:Preg[8]  = 132; F:Resp[8,1]  = 2; F:Resp[8,2]  = 1; F:Resp[8,3]  = 0
  F:Preg[9]  = 133; F:Resp[9,1]  = 2; F:Resp[9,2]  = 1; F:Resp[9,3]  = 0
  F:Preg[10] = 157; F:Resp[10,1] = 0; F:Resp[10,2] = 1; F:Resp[10,3] = 2
  F:Preg[11] = 158; F:Resp[11,1] = 0; F:Resp[11,2] = 1; F:Resp[11,3] = 2
  F:Preg[12] = 182; F:Resp[12,1] = 2; F:Resp[12,2] = 1; F:Resp[12,3] = 0
  F:Preg[13] = 183; F:Resp[13,1] = 2; F:Resp[13,2] = 1; F:Resp[13,3] = 0

  ! Factor G
  G:Preg[1]  =   9; G:Resp[1,1]  = 0; G:Resp[1,2]  = 1; G:Resp[1,3]  = 2
  G:Preg[2]  =  34; G:Resp[2,1]  = 0; G:Resp[2,2]  = 1; G:Resp[2,3]  = 2
  G:Preg[3]  =  59; G:Resp[3,1]  = 0; G:Resp[3,2]  = 1; G:Resp[3,3]  = 2
  G:Preg[4]  =  84; G:Resp[4,1]  = 0; G:Resp[4,2]  = 1; G:Resp[4,3]  = 2
  G:Preg[5]  = 109; G:Resp[5,1]  = 2; G:Resp[5,2]  = 1; G:Resp[5,3]  = 0
  G:Preg[6]  = 134; G:Resp[6,1]  = 2; G:Resp[6,2]  = 1; G:Resp[6,3]  = 0
  G:Preg[7]  = 159; G:Resp[7,1]  = 0; G:Resp[7,2]  = 1; G:Resp[7,3]  = 2
  G:Preg[8]  = 160; G:Resp[8,1]  = 2; G:Resp[8,2]  = 1; G:Resp[8,3]  = 0
  G:Preg[9]  = 184; G:Resp[9,1]  = 2; G:Resp[9,2]  = 1; G:Resp[9,3]  = 0
  G:Preg[10] = 185; G:Resp[10,1] = 2; G:Resp[10,2] = 1; G:Resp[10,3] = 0

  ! Factor H
  H:Preg[1]  =  10; H:Resp[1,1]  = 2; H:Resp[1,2]  = 1; H:Resp[1,3]  = 0
  H:Preg[2]  =  35; H:Resp[2,1]  = 0; H:Resp[2,2]  = 1; H:Resp[2,3]  = 2
  H:Preg[3]  =  36; H:Resp[3,1]  = 2; H:Resp[3,2]  = 1; H:Resp[3,3]  = 0
  H:Preg[4]  =  60; H:Resp[4,1]  = 0; H:Resp[4,2]  = 1; H:Resp[4,3]  = 2
  H:Preg[5]  =  61; H:Resp[5,1]  = 0; H:Resp[5,2]  = 1; H:Resp[5,3]  = 2
  H:Preg[6]  =  85; H:Resp[6,1]  = 0; H:Resp[6,2]  = 1; H:Resp[6,3]  = 2
  H:Preg[7]  =  86; H:Resp[7,1]  = 0; H:Resp[7,2]  = 1; H:Resp[7,3]  = 2
  H:Preg[8]  = 110; H:Resp[8,1]  = 2; H:Resp[8,2]  = 1; H:Resp[8,3]  = 0
  H:Preg[9]  = 111; H:Resp[9,1]  = 2; H:Resp[9,2]  = 1; H:Resp[9,3]  = 0
  H:Preg[10] = 135; H:Resp[10,1] = 2; H:Resp[10,2] = 1; H:Resp[10,3] = 0
  H:Preg[11] = 136; H:Resp[11,1] = 2; H:Resp[11,2] = 1; H:Resp[11,3] = 0
  H:Preg[12] = 161; H:Resp[12,1] = 0; H:Resp[12,2] = 1; H:Resp[12,3] = 2
  H:Preg[13] = 186; H:Resp[13,1] = 2; H:Resp[13,2] = 1; H:Resp[13,3] = 0

  ! Factor I
  I:Preg[1]  =  11; I:Resp[1,1]  = 0; I:Resp[1,2]  = 1; I:Resp[1,3]  = 2
  I:Preg[2]  =  12; I:Resp[2,1]  = 2; I:Resp[2,2]  = 1; I:Resp[2,3]  = 0
  I:Preg[3]  =  37; I:Resp[3,1]  = 2; I:Resp[3,2]  = 1; I:Resp[3,3]  = 0
  I:Preg[4]  =  62; I:Resp[4,1]  = 0; I:Resp[4,2]  = 1; I:Resp[4,3]  = 2
  I:Preg[5]  =  87; I:Resp[5,1]  = 0; I:Resp[5,2]  = 1; I:Resp[5,3]  = 2
  I:Preg[6]  = 112; I:Resp[6,1]  = 2; I:Resp[6,2]  = 1; I:Resp[6,3]  = 0
  I:Preg[7]  = 137; I:Resp[7,1]  = 0; I:Resp[7,2]  = 1; I:Resp[7,3]  = 2
  I:Preg[8]  = 138; I:Resp[8,1]  = 2; I:Resp[8,2]  = 1; I:Resp[8,3]  = 0
  I:Preg[9]  = 162; I:Resp[9,1]  = 0; I:Resp[9,2]  = 1; I:Resp[9,3]  = 2
  I:Preg[10] = 163; I:Resp[10,1] = 2; I:Resp[10,2] = 1; I:Resp[10,3] = 0

  ! Factor L
  L:Preg[1]  =  13; L:Resp[1,1]  = 0; L:Resp[1,2]  = 1; L:Resp[1,3]  = 2
  L:Preg[2]  =  38; L:Resp[2,1]  = 2; L:Resp[2,2]  = 1; L:Resp[2,3]  = 0
  L:Preg[3]  =  63; L:Resp[3,1]  = 0; L:Resp[3,2]  = 1; L:Resp[3,3]  = 2
  L:Preg[4]  =  64; L:Resp[4,1]  = 0; L:Resp[4,2]  = 1; L:Resp[4,3]  = 2
  L:Preg[5]  =  88; L:Resp[5,1]  = 2; L:Resp[5,2]  = 1; L:Resp[5,3]  = 0
  L:Preg[6]  =  89; L:Resp[6,1]  = 0; L:Resp[6,2]  = 1; L:Resp[6,3]  = 2
  L:Preg[7]  = 113; L:Resp[7,1]  = 2; L:Resp[7,2]  = 1; L:Resp[7,3]  = 0
  L:Preg[8]  = 114; L:Resp[8,1]  = 2; L:Resp[8,2]  = 1; L:Resp[8,3]  = 0
  L:Preg[9]  = 139; L:Resp[9,1]  = 0; L:Resp[9,2]  = 1; L:Resp[9,3]  = 2
  L:Preg[10] = 164; L:Resp[10,1] = 2; L:Resp[10,2] = 1; L:Resp[10,3] = 0

  ! Factor M
  M:Preg[1]  =  14; M:Resp[1,1]  = 0; M:Resp[1,2]  = 1; M:Resp[1,3]  = 2
  M:Preg[2]  =  15; M:Resp[2,1]  = 0; M:Resp[2,2]  = 1; M:Resp[2,3]  = 2
  M:Preg[3]  =  39; M:Resp[3,1]  = 2; M:Resp[3,2]  = 1; M:Resp[3,3]  = 0
  M:Preg[4]  =  40; M:Resp[4,1]  = 2; M:Resp[4,2]  = 1; M:Resp[4,3]  = 0
  M:Preg[5]  =  65; M:Resp[5,1]  = 2; M:Resp[5,2]  = 1; M:Resp[5,3]  = 0
  M:Preg[6]  =  90; M:Resp[6,1]  = 0; M:Resp[6,2]  = 1; M:Resp[6,3]  = 2
  M:Preg[7]  =  91; M:Resp[7,1]  = 2; M:Resp[7,2]  = 1; M:Resp[7,3]  = 0
  M:Preg[8]  = 115; M:Resp[8,1]  = 2; M:Resp[8,2]  = 1; M:Resp[8,3]  = 0
  M:Preg[9]  = 116; M:Resp[9,1]  = 2; M:Resp[9,2]  = 1; M:Resp[9,3]  = 0
  M:Preg[10] = 140; M:Resp[10,1] = 2; M:Resp[10,2] = 1; M:Resp[10,3] = 0
  M:Preg[11] = 141; M:Resp[11,1] = 0; M:Resp[11,2] = 1; M:Resp[11,3] = 2
  M:Preg[12] = 165; M:Resp[12,1] = 0; M:Resp[12,2] = 1; M:Resp[12,3] = 2
  M:Preg[13] = 166; M:Resp[13,1] = 0; M:Resp[13,2] = 1; M:Resp[13,3] = 2

  ! Factor N
  N:Preg[1]  =  16; N:Resp[1,1]  = 0; N:Resp[1,2]  = 1; N:Resp[1,3]  = 2
  N:Preg[2]  =  17; N:Resp[2,1]  = 2; N:Resp[2,2]  = 1; N:Resp[2,3]  = 0
  N:Preg[3]  =  41; N:Resp[3,1]  = 0; N:Resp[3,2]  = 1; N:Resp[3,3]  = 2
  N:Preg[4]  =  42; N:Resp[4,1]  = 2; N:Resp[4,2]  = 1; N:Resp[4,3]  = 0
  N:Preg[5]  =  66; N:Resp[5,1]  = 0; N:Resp[5,2]  = 1; N:Resp[5,3]  = 2
  N:Preg[6]  =  67; N:Resp[6,1]  = 0; N:Resp[6,2]  = 1; N:Resp[6,3]  = 2
  N:Preg[7]  =  92; N:Resp[7,1]  = 0; N:Resp[7,2]  = 1; N:Resp[7,3]  = 2
  N:Preg[8]  = 117; N:Resp[8,1]  = 2; N:Resp[8,2]  = 1; N:Resp[8,3]  = 0
  N:Preg[9]  = 142; N:Resp[9,1]  = 2; N:Resp[9,2]  = 1; N:Resp[9,3]  = 0
  N:Preg[10] = 167; N:Resp[10,1] = 2; N:Resp[10,2] = 1; N:Resp[10,3] = 0

  ! Factor O
  O:Preg[1]  =  18; O:Resp[1,1]  = 2; O:Resp[1,2]  = 1; O:Resp[1,3]  = 0
  O:Preg[2]  =  19; O:Resp[2,1]  = 0; O:Resp[2,2]  = 1; O:Resp[2,3]  = 2
  O:Preg[3]  =  43; O:Resp[3,1]  = 2; O:Resp[3,2]  = 1; O:Resp[3,3]  = 0
  O:Preg[4]  =  44; O:Resp[4,1]  = 0; O:Resp[4,2]  = 1; O:Resp[4,3]  = 2
  O:Preg[5]  =  68; O:Resp[5,1]  = 0; O:Resp[5,2]  = 1; O:Resp[5,3]  = 2
  O:Preg[6]  =  69; O:Resp[6,1]  = 2; O:Resp[6,2]  = 1; O:Resp[6,3]  = 0
  O:Preg[7]  =  93; O:Resp[7,1]  = 0; O:Resp[7,2]  = 1; O:Resp[7,3]  = 2
  O:Preg[8]  =  94; O:Resp[8,1]  = 2; O:Resp[8,2]  = 1; O:Resp[8,3]  = 0
  O:Preg[9]  = 118; O:Resp[9,1]  = 2; O:Resp[9,2]  = 1; O:Resp[9,3]  = 0
  O:Preg[10] = 119; O:Resp[10,1] = 2; O:Resp[10,2] = 1; O:Resp[10,3] = 0
  O:Preg[11] = 143; O:Resp[11,1] = 2; O:Resp[11,2] = 1; O:Resp[11,3] = 0
  O:Preg[12] = 144; O:Resp[12,1] = 0; O:Resp[12,2] = 1; O:Resp[12,3] = 2
  O:Preg[13] = 168; O:Resp[13,1] = 0; O:Resp[13,2] = 1; O:Resp[13,3] = 2

  ! Factor Q1
  Q1:Preg[1]  =  20; Q1:Resp[1,1]  = 2; Q1:Resp[1,2]  = 1; Q1:Resp[1,3]  = 0
  Q1:Preg[2]  =  21; Q1:Resp[2,1]  = 0; Q1:Resp[2,2]  = 1; Q1:Resp[2,3]  = 2
  Q1:Preg[3]  =  45; Q1:Resp[3,1]  = 0; Q1:Resp[3,2]  = 1; Q1:Resp[3,3]  = 2
  Q1:Preg[4]  =  46; Q1:Resp[4,1]  = 2; Q1:Resp[4,2]  = 1; Q1:Resp[4,3]  = 0
  Q1:Preg[5]  =  70; Q1:Resp[5,1]  = 2; Q1:Resp[5,2]  = 1; Q1:Resp[5,3]  = 0
  Q1:Preg[6]  =  95; Q1:Resp[6,1]  = 0; Q1:Resp[6,2]  = 1; Q1:Resp[6,3]  = 2
  Q1:Preg[7]  = 120; Q1:Resp[7,1]  = 0; Q1:Resp[7,2]  = 1; Q1:Resp[7,3]  = 2
  Q1:Preg[8]  = 145; Q1:Resp[8,1]  = 2; Q1:Resp[8,2]  = 1; Q1:Resp[8,3]  = 0
  Q1:Preg[9]  = 169; Q1:Resp[9,1]  = 2; Q1:Resp[9,2]  = 1; Q1:Resp[9,3]  = 0
  Q1:Preg[10] = 170; Q1:Resp[10,1] = 0; Q1:Resp[10,2] = 1; Q1:Resp[10,3] = 2

  ! Factor Q2
  Q2:Preg[1]  =  22; Q2:Resp[1,1]  = 0; Q2:Resp[1,2]  = 1; Q2:Resp[1,3]  = 2
  Q2:Preg[2]  =  47; Q2:Resp[2,1]  = 2; Q2:Resp[2,2]  = 1; Q2:Resp[2,3]  = 0
  Q2:Preg[3]  =  71; Q2:Resp[3,1]  = 2; Q2:Resp[3,2]  = 1; Q2:Resp[3,3]  = 0
  Q2:Preg[4]  =  72; Q2:Resp[4,1]  = 2; Q2:Resp[4,2]  = 1; Q2:Resp[4,3]  = 0
  Q2:Preg[5]  =  96; Q2:Resp[5,1]  = 0; Q2:Resp[5,2]  = 1; Q2:Resp[5,3]  = 2
  Q2:Preg[6]  =  97; Q2:Resp[6,1]  = 0; Q2:Resp[6,2]  = 1; Q2:Resp[6,3]  = 2
  Q2:Preg[7]  = 121; Q2:Resp[7,1]  = 0; Q2:Resp[7,2]  = 1; Q2:Resp[7,3]  = 2
  Q2:Preg[8]  = 122; Q2:Resp[8,1]  = 0; Q2:Resp[8,2]  = 1; Q2:Resp[8,3]  = 2
  Q2:Preg[9]  = 146; Q2:Resp[9,1]  = 2; Q2:Resp[9,2]  = 1; Q2:Resp[9,3]  = 0
  Q2:Preg[10] = 171; Q2:Resp[10,1] = 2; Q2:Resp[10,2] = 1; Q2:Resp[10,3] = 0

  ! Factor Q3
  Q3:Preg[1]  =  23; Q3:Resp[1,1]  = 0; Q3:Resp[1,2]  = 1; Q3:Resp[1,3]  = 2
  Q3:Preg[2]  =  24; Q3:Resp[2,1]  = 0; Q3:Resp[2,2]  = 1; Q3:Resp[2,3]  = 2
  Q3:Preg[3]  =  48; Q3:Resp[3,1]  = 2; Q3:Resp[3,2]  = 1; Q3:Resp[3,3]  = 0
  Q3:Preg[4]  =  73; Q3:Resp[4,1]  = 2; Q3:Resp[4,2]  = 1; Q3:Resp[4,3]  = 0
  Q3:Preg[5]  =  98; Q3:Resp[5,1]  = 2; Q3:Resp[5,2]  = 1; Q3:Resp[5,3]  = 0
  Q3:Preg[6]  = 123; Q3:Resp[6,1]  = 0; Q3:Resp[6,2]  = 1; Q3:Resp[6,3]  = 2
  Q3:Preg[7]  = 147; Q3:Resp[7,1]  = 0; Q3:Resp[7,2]  = 1; Q3:Resp[7,3]  = 2
  Q3:Preg[8]  = 148; Q3:Resp[8,1]  = 2; Q3:Resp[8,2]  = 1; Q3:Resp[8,3]  = 0
  Q3:Preg[9]  = 172; Q3:Resp[9,1]  = 0; Q3:Resp[9,2]  = 1; Q3:Resp[9,3]  = 2
  Q3:Preg[10] = 173; Q3:Resp[10,1] = 2; Q3:Resp[10,2] = 1; Q3:Resp[10,3] = 0

  ! Factor Q4
  Q4:Preg[1]  =  25; Q4:Resp[1,1]  = 0; Q4:Resp[1,2]  = 1; Q4:Resp[1,3]  = 2
  Q4:Preg[2]  =  49; Q4:Resp[2,1]  = 2; Q4:Resp[2,2]  = 1; Q4:Resp[2,3]  = 0
  Q4:Preg[3]  =  50; Q4:Resp[3,1]  = 2; Q4:Resp[3,2]  = 1; Q4:Resp[3,3]  = 0
  Q4:Preg[4]  =  74; Q4:Resp[4,1]  = 2; Q4:Resp[4,2]  = 1; Q4:Resp[4,3]  = 0
  Q4:Preg[5]  =  75; Q4:Resp[5,1]  = 0; Q4:Resp[5,2]  = 1; Q4:Resp[5,3]  = 2
  Q4:Preg[6]  =  99; Q4:Resp[6,1]  = 2; Q4:Resp[6,2]  = 1; Q4:Resp[6,3]  = 0
  Q4:Preg[7]  = 100; Q4:Resp[7,1]  = 0; Q4:Resp[7,2]  = 1; Q4:Resp[7,3]  = 2
  Q4:Preg[8]  = 124; Q4:Resp[8,1]  = 2; Q4:Resp[8,2]  = 1; Q4:Resp[8,3]  = 0
  Q4:Preg[9]  = 125; Q4:Resp[9,1]  = 0; Q4:Resp[9,2]  = 1; Q4:Resp[9,3]  = 2
  Q4:Preg[10] = 149; Q4:Resp[10,1] = 2; Q4:Resp[10,2] = 1; Q4:Resp[10,3] = 0
  Q4:Preg[11] = 150; Q4:Resp[11,1] = 0; Q4:Resp[11,2] = 1; Q4:Resp[11,3] = 2
  Q4:Preg[12] = 174; Q4:Resp[12,1] = 2; Q4:Resp[12,2] = 1; Q4:Resp[12,3] = 0
  Q4:Preg[13] = 175; Q4:Resp[13,1] = 0; Q4:Resp[13,2] = 1; Q4:Resp[13,3] = 2

  ! Si alguna pregunta no fue respondida regresamos
  loop index = 1 to 187
    if tes:respuesta[index] <> 'A' and tes:respuesta[index] <> 'B' and tes:respuesta[index] <> 'C'
      return
    .
  .

  ! Limpiamos raw y sten
  loop index = 1 to 16
    tes:raw[index] = 0
    tes:sten[index] = 0
  .
  ! Procesamos las respuestas y obtenemos la puntuacion cruda
  loop index = 1 to 10
    tes:raw[1]  += A:Resp[index, val(tes:respuesta[A:Preg[index]]) - 65 + 1]
    tes:raw[6]  += G:Resp[index, val(tes:respuesta[G:Preg[index]]) - 65 + 1]
    tes:raw[8]  += I:Resp[index, val(tes:respuesta[I:Preg[index]]) - 65 + 1]
    tes:raw[9]  += L:Resp[index, val(tes:respuesta[L:Preg[index]]) - 65 + 1]
    tes:raw[11] += N:Resp[index, val(tes:respuesta[N:Preg[index]]) - 65 + 1]
    tes:raw[13] += Q1:Resp[index, val(tes:respuesta[Q1:Preg[index]]) - 65 + 1]
    tes:raw[14] += Q2:Resp[index, val(tes:respuesta[Q2:Preg[index]]) - 65 + 1]
    tes:raw[15] += Q3:Resp[index, val(tes:respuesta[Q3:Preg[index]]) - 65 + 1]
  .
  loop index = 1 to 13
    tes:raw[2]  += B:Resp[index, val(tes:respuesta[B:Preg[index]]) - 65 + 1]
    tes:raw[3]  += C:Resp[index, val(tes:respuesta[C:Preg[index]]) - 65 + 1]
    tes:raw[4]  += E:Resp[index, val(tes:respuesta[E:Preg[index]]) - 65 + 1]
    tes:raw[5]  += F:Resp[index, val(tes:respuesta[F:Preg[index]]) - 65 + 1]
    tes:raw[7]  += H:Resp[index, val(tes:respuesta[H:Preg[index]]) - 65 + 1]
    tes:raw[10] += M:Resp[index, val(tes:respuesta[M:Preg[index]]) - 65 + 1]
    tes:raw[12] += O:Resp[index, val(tes:respuesta[O:Preg[index]]) - 65 + 1]
    tes:raw[16] += Q4:Resp[index, val(tes:respuesta[Q4:Preg[index]]) - 65 + 1]
  .

  ! Leemos la tabla de la norma a usar
  nor:codigo = tes:norma
  get(normas, nor:Por_codigo)
  IF ERROR()
    STOP('UpdRawSten: ' & ERROR())
    return
  .

  ! Procesamos puntuacion cruda y obtenemos el sten
  loop index = 1 to 16
    loop index_1 = 1 to 10
      if(tes:raw[index] <= nor:puntos_h[(index_1 - 1) * 16 + index] and tes:raw[index] >= nor:puntos_l[(index_1 - 1) * 16 + index])
        tes:sten[index] = index_1
      .
    .
  .



Upd_Test     PROCEDURE
! OJO:> poner un #define para las coordenadas

SCREEN       Screen       Window(18,80),At(7,1),Pre(SCR),Hue(14,1)
               Row(1,1)   String('…Õ{78}ª')
               Row(2,1)   Repeat(16);String('∫<0{78}>∫') .
               Row(18,1)  String('»Õ{78}º')
               Row(8,56)  Entry,Use(?FIRST_FIELD)
                 Col(74)  Entry,Use(?LAST_FIELD)
                 Col(74)  Pause(''),Use(?DELETE_FIELD)
             .

TABLE        TABLE,PRE(SAV)
SAVE_RECORD    GROUP;BYTE,DIM(SIZE(Tes:RECORD)).
             .
index short
index_1 short

  CODE

  index = 0
  index_1 = 0
  OPEN(SCREEN)                                   !OPEN THE SCREEN
  SETCURSOR                                      !TURN OFF ANY CURSOR
  SAVE_RECORD = Tes:RECORD                       !SAVE THE ORIGINAL
  ADD(TABLE,1)                                   !STORE IN MEMORY TABLE
  IF ACTION = 5                                  !AUTONUMBER ACTION
    DISK_ACTN# = 2                               !  SET FOR PHYSICAL ACTION
    ACTION = 1                                   !  SET FOR LOGICAL ACTION
  ELSE                                           !OTHERWISE
    DISK_ACTN# = ACTION                          !  SET ACTION FOR DISK WRITE
  .

  EXECUTE DISK_ACTN#                             !SET THE CURRENT RECORD POINTER
    POINTER# = 0                                 !  NO RECORD FOR ADD
    POINTER# = POINTER(Test)                     !  CURRENT RECORD FOR CHANGE
    POINTER# = POINTER(Test)                     !  CURRENT RECORD FOR CHANGE (deletion)
  .
  ACTION# = ACTION                               !STORE REQUIRED ACTION
  LOOP                                           !LOOP THRU ALL THE FIELDS
    MEM:MESSAGE = CENTER(MEM:MESSAGE,SIZE(MEM:MESSAGE)) !DISPLAY ACTION MESSAGE
    DO CALCFIELDS                                !CALCULATE DISPLAY FIELDS
    ALERT                                        !RESET ALERTED KEYS
    ALERT(ACCEPT_KEY)                            !ALERT SCREEN ACCEPT KEY
    ALERT(REJECT_KEY)                            !ALERT SCREEN REJECT KEY
    alert(f4_key)
    alert(f5_key)
    alert(alt_f10)
    ACCEPT                                       !READ A FIELD
    if keycode() = f4_key   ! calendario
      Calendario
      SELECT(?)                    ! SELECT THIS ENTRY
      CYCLE  ! ® hace falta esto ???
    .
    if keycode() = f5_key   ! control
      Control
      SELECT(?)             ! SELECT THIS ENTRY
      CYCLE  ! ® hace falta esto ???
    .
    if keycode() = alt_f10         ! ® Salir del sistema ?
      ConfSalida
      SELECT(?)                    ! SELECT THIS ENTRY
      CYCLE
    .

    IF KEYCODE() = REJECT_KEY THEN BREAK.        !RETURN ON SCREEN REJECT KEY
    EXECUTE ACTION                               !SET MESSAGE
      MEM:MESSAGE = 'Se agregar† el registro'    !
      MEM:MESSAGE = 'Se modificar† el registro'  !
      MEM:MESSAGE = 'Se borrar† el registro'     !
    .
    IF KEYCODE() = ACCEPT_KEY                    !ON SCREEN ACCEPT KEY
      UPDATE                                     !  MOVE ALL FIELDS FROM SCREEN
      SELECT(?)                                  !  START WITH CURRENT FIELD
      SELECT                                     !  EDIT ALL FIELDS
      CYCLE                                      !  GO TO TOP OF LOOP
    .
    CASE FIELD()                                 !JUMP TO FIELD EDIT ROUTINE
    OF ?FIRST_FIELD                              !FROM THE FIRST FIELD
      IF KEYCODE() = ESC_KEY THEN BREAK.         !  RETURN ON ESC KEY
      IF ACTION = 3 THEN SELECT(?DELETE_FIELD).!    OR CONFIRM FOR DELETE


    OF ?LAST_FIELD                               !FROM THE LAST FIELD

    ! Actualizamos el array de respuestas
      loop index = 1 to 187
        if tes:respuesta[index] <> 'A' and tes:respuesta[index] <> 'B' and tes:respuesta[index] <> 'C' then tes:respuesta[index] = '?'.
        sethue(7,1)
        show(8 + (1 - 1) + 2 * int((index - 1) / 25), 4 + 3 * int((index - 1) % 25), index % 100, @n02)
        sethue(15,1)
        show(9 + (1 - 1) + 2 * int((index - 1) / 25), 4 + 3 * int((index - 1) % 25), tes:respuesta[index], @s1)
      .
      sethue

    ! Aceptamos las respuestas
    ! Los ALERT por rango porque CLARION solo acepta 16 alerts
      alert(49, 51)       ! '1' - '3'
      alert(65, 67)       ! 'A' - 'C'
      alert(97, 99)       ! 'a' - 'c'
      alert(left_key)
      alert(up_key)
      alert(down_key)
      alert(right_key)

      loop index = 1 to 187
        sethue(15,1)
        ask(9 + (1 - 1) + 2 * int((index - 1) / 25), 4 + 3 * int((index - 1) % 25), tes:respuesta[index], @s1)
        sethue

        if keycode() = REJECT_KEY then return.
        if keycode() = ACCEPT_KEY then break.

        if keycode() >= 65 and keycode() <= 67               ! entre 'A' y 'C'
          tes:respuesta[index] = chr(keycode())
          show(9 + (1 - 1) + 2 * int((index - 1) / 25), 4 + 3 * int((index - 1) % 25), tes:respuesta[index], @s1)
          if(mem:Pito = 'S') then beep(100,10).
          CYCLE
        .
        if keycode() >= 97 and keycode() <= 99               ! entre 'a' y 'c'
          tes:respuesta[index] = chr(keycode() - 97 + 65)
          show(9 + (1 - 1) + 2 * int((index - 1) / 25), 4 + 3 * int((index - 1) % 25), tes:respuesta[index], @s1)
          if(mem:Pito = 'S') then beep(100,10).
          CYCLE
        .
        if keycode() >= 49 and keycode() <= 51               ! entre '1' y '3'
          tes:respuesta[index] = chr(keycode() - 49 + 65)
          show(9 + (1 - 1) + 2 * int((index - 1) / 25), 4 + 3 * int((index - 1) % 25), tes:respuesta[index], @s1)
          if(mem:Pito = 'S') then beep(100,10).
          CYCLE
        .
        if keycode() = alt_f10         ! ® Salir del sistema ?
          ConfSalida
          index -= 1                   ! SELECT THIS ENTRY
          CYCLE
        .
        if keycode() = left_key or keycode() = esc_key
          index -= 2
          if(index < 0)
            index = 186
          .
          CYCLE
        .
        if keycode() = right_key
          if(index > 186)
            index = 0
          .
          CYCLE
        .
        if keycode() = up_key
          index -= 26
          if(index < 0) then
            index += 199
            if(index >= 187) then
              index -= 25
            .
          .
          CYCLE
        .
        if keycode() = down_key
          index += 24
          if(index >= 187) then
            index -= 199
              if(index <= 0) then
                index += 25
              .
          .
          CYCLE
        .
      .

      ! Actualizamos Raw y Sten
      UpdRawSten()

      IF ACTION = 2 OR ACTION = 3                !IF UPDATING RECORD
        SAVE_RECORD = Tes:RECORD                 !  SAVE CURRENT CHANGES
        ADD(TABLE,2)                             !  STORE IN MEMORY TABLE
        GET(TABLE,1)                             !  RETRIEVE ORIGINAL RECORD
        HOLD(Test)                               !  HOLD FILE
        GET(Test,POINTER#)                       !  RE-READ SAME RECORD
        IF ERRORCODE() = 35                      !  IF RECORD WAS DELETED
          IF DISK_ACTN# = 2                      !  IF TRYING TO UPDATE
             DISK_ACTN# = 1                      !    THEN ADD IT BACK
          ELSE                                   !
             RELEASE(Test)                       !  RELEASE FILE
             ACTION = 0                          !  TURN OFF ACTION
          .
        ELSIF |                                  !OTHERWISE
          Tes:RECORD <> SAVE_RECORD              !    BY ANOTHER STATION
          MEM:MESSAGE = 'Cambiado por otra estaci¢n' !INFORM USER
          SELECT(2)                              !  GO BACK TO TOP OF FORM
          BEEP                                   !  SOUND ALARM
          RELEASE(Test)                          !  RELEASE FILE
          SAVE_RECORD = Tes:RECORD               !  SAVE RECORD
          DISPLAY                                !  DISPLAY THE FIELDS
          PUT(TABLE)                             !  FREE SAVED CHANGES
          CYCLE                                  !  AND CONTINUE
        .
        GET(TABLE,2)                             !  READ CURRENT (CHANGED) REC
        Tes:RECORD = SAVE_RECORD                 !  MOVE RECORD
        DELETE(TABLE)                            !  DELETE MEMORY TABLE ITEM
      .
      EXECUTE DISK_ACTN#                         !  UPDATE THE FILE
        ADD(Test)                                !    ADD NEW RECORD
        PUT(Test)                                !    CHANGE EXISTING RECORD
        DELETE(Test)                             !    DELETE EXISTING RECORD
      .
      IF ERRORCODE() = 40                        !  DUPLICATE KEY ERROR
        MEM:MESSAGE = ERROR()                    !    DISPLAY ERR MESSAGE
        SELECT(2)                                !    POSITION TO TOP OF FORM
        IF ACTION = 2 THEN RELEASE(Test). !           RELEASE HELD RECORD
        CYCLE                                    !    GET OUT OF EDIT LOOP
      ELSIF ERROR()                              !  CHECK FOR UNEXPECTED ERROR
        STOP(ERROR())                            !    HALT EXECUTION
      .
      IF ACTION = 1 THEN POINTER# = POINTER(Test). !POINT TO RECORD
      SAVE_RECORD = Tes:RECORD                   !  NEW ORIGINAL
      ACTION = ACTION#                           !  RETRIEVE ORIGINAL OPERATION
      ACTION = 0                                 !  SET ACTION TO COMPLETE
      BREAK                                      !  AND RETURN TO CALLER

    OF ?DELETE_FIELD                             !FROM THE DELETE FIELD
      IF KEYCODE() = ENTER_KEY |                 !  ON ENTER KEY
      OR KEYCODE() = ACCEPT_KEY                  !  OR CTRL-ENTER KEY
        SELECT(?LAST_FIELD)                      !    DELETE THE RECORD
      ELSE                                       !  OTHERWISE
        BEEP                                     !    BEEP AND ASK AGAIN
  . . .
  FREE(TABLE)                                    !  RELEASE MEMORY TABLE
  RETURN                                         !  AND RETURN TO CALLER

CALCFIELDS   ROUTINE
  IF FIELD() > ?FIRST_FIELD                      !BEYOND FIRST_FIELD?
    IF KEYCODE() = 0 AND SELECTED() > FIELD() THEN EXIT. !GET OUT IF NOT NONSTOP
  .


Upd_Normas   PROCEDURE

SCREEN       Screen       Window(24,78),At(2,2),Pre(SCR),Hue(14,1)
               Row(1,1)   String('…Õ{76}ª')
               Row(2,1)   Repeat(4);String('∫<0{76}>∫') .
               Row(6,1)   String('«ƒ{76}∂')
               Row(7,1)   Repeat(17);String('∫<0{76}>∫') .
               Row(24,1)  String('»Õ{76}º')
               Row(2,29)  String('Actualizacion de Normas')
               Row(7,10)  String('1 {6}2 {6}3 {6}4 {6}5 {6}6 {6}7 {6}8 {6}9 {6}10')
               Row(8,4)   String('A')
                 Col(10)  String('-')
                 Col(17)  String('- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(9,4)   String('B {5}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(10,4)  String('C {5}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(11,4)  String('E {5}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(12,4)  String('F {5}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(13,4)  String('G {5}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(14,4)  String('H {5}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(15,4)  String('I {5}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(16,4)  String('L {5}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(17,4)  String('M {5}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(18,4)  String('N {5}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(19,4)  String('O {5}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(20,4)  String('Q1    - {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(21,4)  String('Q2    - {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(22,4)  String('Q3    - {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
               Row(23,4)  String('Q4    - {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}- {6}-')
MESSAGE        Row(3,25)  String(30),Enh
               Row(2,55)  Entry,Use(?FIRST_FIELD)
               Row(4,4)   String('Codigo {5}:')
                 Col(16)  Entry(@N5),Use(Nor:Codigo),Ins,Req,Num
               Row(5,4)   String('Descripcion:')
                 Col(16)  Entry(@S60),Use(Nor:Descripcion),Req,Lft
                          Repeat(16,10),Every(1,7),Index(index)
               Row(8,8)     Entry(@N02),Use(nor:Puntos_L),Imm
                 Col(11)    Entry(@N02),Use(nor:Puntos_H),Imm
                          .
               Row(4,76)  Entry,Use(?LAST_FIELD)
                 Col(76)  Pause(''),Use(?DELETE_FIELD)
             .

TABLE        TABLE,PRE(SAV)
SAVE_RECORD    GROUP;BYTE,DIM(SIZE(Nor:RECORD)).
             .
index byte

  EJECT
  CODE
  idle
  OPEN(SCREEN)                                   !OPEN THE SCREEN
  SETCURSOR                                      !TURN OFF ANY CURSOR
  SAVE_RECORD = Nor:RECORD                       !SAVE THE ORIGINAL
  ADD(TABLE,1)                                   !STORE IN MEMORY TABLE
  IF ACTION = 5                                  !AUTONUMBER ACTION
    DISK_ACTN# = 2                               !  SET FOR PHYSICAL ACTION
    ACTION = 1                                   !  SET FOR LOGICAL ACTION
  ELSE                                           !OTHERWISE
    DISK_ACTN# = ACTION                          !  SET ACTION FOR DISK WRITE
  .
  loop index = 1 to 160
    display()
  .

  EXECUTE DISK_ACTN#                             !SET THE CURRENT RECORD POINTER
    POINTER# = 0                                 !  NO RECORD FOR ADD
    POINTER# = POINTER(Normas)                   !  CURRENT RECORD FOR CHANGE
    POINTER# = POINTER(Normas)                   !  CURRENT RECORD FOR CHANGE (deletion)
  .
  ACTION# = ACTION                               !STORE REQUIRED ACTION
  LOOP                                           !LOOP THRU ALL THE FIELDS
    MEM:MESSAGE = CENTER(MEM:MESSAGE,SIZE(MEM:MESSAGE)) !DISPLAY ACTION MESSAGE
    DO CALCFIELDS                                !CALCULATE DISPLAY FIELDS
    ALERT                                        !RESET ALERTED KEYS
    ALERT(ACCEPT_KEY)                            !ALERT SCREEN ACCEPT KEY
    ALERT(REJECT_KEY)                            !ALERT SCREEN REJECT KEY
    alert(f4_key)
    alert(f5_key)
    alert(alt_f10)
    ACCEPT                                       !READ A FIELD
    if keycode() = f4_key   ! calendario
      Calendario
      SELECT(?)                    ! SELECT THIS ENTRY
      CYCLE  ! ® hace falta esto ???
    .
    if keycode() = f5_key   ! control
      Control
      SELECT(?)             ! SELECT THIS ENTRY
      CYCLE  ! ® hace falta esto ???
    .
    if keycode() = alt_f10         ! ® Salir del sistema ?
      ConfSalida
      SELECT(?)                    ! SELECT THIS ENTRY
      CYCLE
    .
    IF KEYCODE() = REJECT_KEY THEN BREAK.        !RETURN ON SCREEN REJECT KEY
    EXECUTE ACTION                               !SET MESSAGE
      MEM:MESSAGE = 'Se agregar† el registro'    !
      MEM:MESSAGE = 'Se modificar† el registro'  !
      MEM:MESSAGE = 'Se borrar† el registro'     !
    .
    IF KEYCODE() = ACCEPT_KEY                    !ON SCREEN ACCEPT KEY
      UPDATE                                     !  MOVE ALL FIELDS FROM SCREEN
      SELECT(?)                                  !  START WITH CURRENT FIELD
      SELECT                                     !  EDIT ALL FIELDS
      CYCLE                                      !  GO TO TOP OF LOOP
    .
    CASE FIELD()                                 !JUMP TO FIELD EDIT ROUTINE
    OF ?FIRST_FIELD                              !FROM THE FIRST FIELD
      IF KEYCODE() = ESC_KEY THEN BREAK.         !  RETURN ON ESC KEY
      IF ACTION = 3 THEN SELECT(?DELETE_FIELD).!    OR CONFIRM FOR DELETE

    OF ?Nor:Codigo                               !Codigo de la norma
      IF DUPLICATE(Nor:Por_Codigo)               !  CHECK FOR DUPLICATE KEY
        MEM:MESSAGE = 'Crea entrada duplicada'   !    MOVE AN ERROR MESSAGE
        SELECT(?Nor:Codigo)                      !    STAY ON THE SAME FIELD
        BEEP                                     !    SOUND THE KEYBOARD ALARM
        CYCLE                                    !    AND LOOP AGAIN
      .

    OF ?LAST_FIELD                               !FROM THE LAST FIELD
      IF ACTION = 2 OR ACTION = 3                !IF UPDATING RECORD
        SAVE_RECORD = Nor:RECORD                 !  SAVE CURRENT CHANGES
        ADD(TABLE,2)                             !  STORE IN MEMORY TABLE
        GET(TABLE,1)                             !  RETRIEVE ORIGINAL RECORD
        HOLD(Normas)                             !  HOLD FILE
        GET(Normas,POINTER#)                     !  RE-READ SAME RECORD
        IF ERRORCODE() = 35                      !  IF RECORD WAS DELETED
          IF DISK_ACTN# = 2                      !  IF TRYING TO UPDATE
             DISK_ACTN# = 1                      !    THEN ADD IT BACK
          ELSE                                   !
             RELEASE(Normas)                     !  RELEASE FILE
             ACTION = 0                          !  TURN OFF ACTION
          .
        ELSIF |                                  !OTHERWISE
          Nor:RECORD <> SAVE_RECORD              !    BY ANOTHER STATION
          MEM:MESSAGE = 'Cambiado por otra estaci¢n' !INFORM USER
          SELECT(2)                              !  GO BACK TO TOP OF FORM
          BEEP                                   !  SOUND ALARM
          RELEASE(Normas)                        !  RELEASE FILE
          SAVE_RECORD = Nor:RECORD               !  SAVE RECORD
          DISPLAY                                !  DISPLAY THE FIELDS
          PUT(TABLE)                             !  FREE SAVED CHANGES
          CYCLE                                  !  AND CONTINUE
        .
        GET(TABLE,2)                             !  READ CURRENT (CHANGED) REC
        Nor:RECORD = SAVE_RECORD                 !  MOVE RECORD
        DELETE(TABLE)                            !  DELETE MEMORY TABLE ITEM
      .
      EXECUTE DISK_ACTN#                         !  UPDATE THE FILE
        ADD(Normas)                              !    ADD NEW RECORD
        PUT(Normas)                              !    CHANGE EXISTING RECORD
        DELETE(Normas)                           !    DELETE EXISTING RECORD
      .
      IF ERRORCODE() = 40                        !  DUPLICATE KEY ERROR
        MEM:MESSAGE = ERROR()                    !    DISPLAY ERR MESSAGE
        SELECT(2)                                !    POSITION TO TOP OF FORM
        IF ACTION = 2 THEN RELEASE(Normas). !         RELEASE HELD RECORD
        CYCLE                                    !    GET OUT OF EDIT LOOP
      ELSIF ERROR()                              !  CHECK FOR UNEXPECTED ERROR
        STOP(ERROR())                            !    HALT EXECUTION
      .
      IF ACTION = 1 THEN POINTER# = POINTER(Normas). !POINT TO RECORD
      SAVE_RECORD = Nor:RECORD                   !  NEW ORIGINAL
      ACTION = ACTION#                           !  RETRIEVE ORIGINAL OPERATION
      ACTION = 0                                 !  SET ACTION TO COMPLETE
      BREAK                                      !  AND RETURN TO CALLER

    OF ?DELETE_FIELD                             !FROM THE DELETE FIELD
      IF KEYCODE() = ENTER_KEY |                 !  ON ENTER KEY
      OR KEYCODE() = ACCEPT_KEY                  !  OR CTRL-ENTER KEY
        SELECT(?LAST_FIELD)                      !    DELETE THE RECORD
      ELSE                                       !  OTHERWISE
        BEEP                                     !    BEEP AND ASK AGAIN
  . . .
  FREE(TABLE)                                    !  RELEASE MEMORY TABLE
  idle(hora)
  RETURN                                         !  AND RETURN TO CALLER

CALCFIELDS   ROUTINE
  IF FIELD() > ?FIRST_FIELD                      !BEYOND FIRST_FIELD?
    IF KEYCODE() = 0 AND SELECTED() > FIELD() THEN EXIT. !GET OUT IF NOT NONSTOP
  .
  SCR:MESSAGE = MEM:MESSAGE


X_RESPALDAR  PROCEDURE

SCREEN       Screen       Window(6,57),Hue(15,4)
               Row(1,1)   String('…Õ{55}ª')
               Row(2,1)   Repeat(2),Every(2);String('∫<0{55}>∫') .
               Row(3,1)   String('«ƒ{55}∂')
               Row(5,1)   String('∫<0{55}>∫')
               Row(6,1)   String('»Õ{55}º')
               Row(2,19)  String('Respaldando sus datos')
               Row(4,18)  String('Tiempo estimado:')
!              Row(5,14)  String('Tiempo transcurrido: 03 segundos')
             .
  CODE
    open(SCREEN)

    g_closefiles
    ! ????? g_copyfiles ?
    copy(test, mem:diskette)   ; if(error()) then stop(error()).
    copy(normas, mem:diskette) ; if(error()) then stop(error()).
    copy(espec, mem:diskette)  ; if(error()) then stop(error()).
    copy(titulos, mem:diskette) ; if(error()) then stop(error()).
    copy(cargos, mem:diskette) ; if(error()) then stop(error()).
    copy(universi, mem:diskette) ; if(error()) then stop(error()).
    copy(perfiles, mem:diskette) ; if(error()) then stop(error()).
    g_openfiles


graf function(xNivel)
xNivel   short
str      string(48)
chr      string(1), dim(48), over(str)
i byte

  code

  clear(str)
  Per:codigo = tes:Perfil
  get(Perfiles, Per:Por_codigo)
  ! Dibujamos la barra de resultado del test
  loop i = 1 to 16
    if(xNivel >= 6)
      if(tes:sten[i] >= xNivel)
        chr[i * 3 - 2] = '∞'
      .
    else
      if(tes:sten[i] <= xNivel)
        chr[i * 3 - 2] = '∞'
      .
    .
  ! y la barra del sten optimo
    if(xNivel >= 6)
      if(Per:StenOpt[i] >= xNivel)
        chr[i * 3 - 1] = 'ﬁ'
      .
    else
      if(Per:StenOpt[i] <= xNivel)
        chr[i * 3 - 1] = 'ﬁ'
      .
    .
  .
  loop i = 1 to 16
    chr[i * 3] = '≥'
  .

  ! ??? Esto debe ir en Upd_Test
  tes:Desviacion = MinCuad()
  tes:Desv4 = MinCuad4()
  return(str)



MinCuad function     ! Calculo de los minimos cuadrados
xMinCuad long
i byte

  code
 ! Calculo de minimos cuadrados
  Per:codigo = tes:Perfil
  get(Perfiles, Per:Por_codigo)
  IF ERROR()
    STOP('MinCuad: ' & ERROR())
    return(0)
  .
  xMinCuad = 0
  loop i = 1 to 16
    if(tes:sten[i] <= 0) then
      xMinCuad = Per:MaxDesv
      break
    .
    xMinCuad += Per:Pesos[i] * (tes:sten[i] - Per:StenOpt[i]) ^ 2
  .
  return(100 * (xMinCuad / Per:MaxDesv))



MinCuad4 function    ! Calculo de los minimos cuadrados de los factores
xMinCuad long        !    B, C, G, M
i byte

  code
 ! Calculo de minimos cuadrados
  Per:codigo = tes:Perfil
  get(Perfiles, Per:Por_codigo)
  IF ERROR()
    STOP('MinCuad4: ' & ERROR())
    return(0)
  .
  xMinCuad = 0
  loop i = 1 to 16
    if(tes:sten[i] <= 0) then
      xMinCuad = Per:MaxDesv4
      break
    .
    if(i <> 2 and i <> 3 and i <> 6 and i <> 10)
      cycle     ! No es ninguno de los 4 factores
    .
    xMinCuad += Per:Pesos[i] * (tes:sten[i] - Per:StenOpt[i]) ^ 2
  .
  return(100 * (xMinCuad / Per:MaxDesv4))

CalcGen procedure     ! Calculo general de sten, desv, etc
ddd string(10)
  code
  set(test)
  loop until eof(test)
    next(test)
    show(25, 2, pointer(test), @n04)

  ! Copiamos la cedula que da el CEAP
  ! ddd = sub(tes:cedula_ceap, 3, 10)
  ! tes:ced_nro = deformat(ddd, @p##.###.###p)
  ! tes:ced_voe = tes:cedula_ceap
    show(25, 15, tes:ced_nro, @n08)

  ! Copiamos la especialidad que da el CEAP
  ! pre:codigo = tes:cedula_ceap
  ! get(pre_es, pre:Por_codigo)
  ! IF ERROR()                                      !OPEN RETURNED AN ERROR
  !     STOP('pre_es: ' & ERROR()).                   !  STOP EXECUTION
  ! tes:espec_postul = pre:especi

  ! Eliminamos los que no van  ??????
  ! if(   tes:espec_postul <> '05-180-10' and tes:espec_postul <> '05-181-10' |
  !   and tes:espec_postul <> '05-190-12' and tes:espec_postul <> '05-191-12' |
  !   and tes:espec_postul <> '05-200-12' and tes:espec_postul <> '05-201-12' |
  !   and tes:espec_postul <> '05-210-12' and tes:espec_postul <> '05-211-12')
  !     delete(test)
  !     cycle
  ! .

    ! Actualizamos Raw y Sten
    UpdRawSten()

    ! Actualizacion de los 4 perfiles  ?????? BORRAR
    tes:Perfil = 1
    tes:D1 = MinCuad()
    tes:Perfil = 2
    tes:D2 = MinCuad()
    tes:Perfil = 3
    tes:D3 = MinCuad()
    tes:Perfil = 4
    tes:D4 = MinCuad()

    ! Actualizamos el Perfil
    tes:Perfil = 0
    if(tes:espec_postul = '05-180-10' or tes:espec_postul = '05-181-10') then tes:Perfil = 1.
    if(tes:espec_postul = '05-190-12' or tes:espec_postul = '05-191-12') then tes:Perfil = 2.
    if(tes:espec_postul = '05-200-12' or tes:espec_postul = '05-201-12') then tes:Perfil = 4.
    if(tes:espec_postul = '05-210-12' or tes:espec_postul = '05-211-12') then tes:Perfil = 3.

    ! Actualizamos la Desviacion del perfil
    tes:Desviacion = MinCuad()
    tes:Desv4 = MinCuad4()

    put(test)
    IF ERROR()                                      !Put RETURNED AN ERROR
        STOP('Test: ' & ERROR())                    !  STOP EXECUTION
    .
  .
  return


GetPerfil Procedure
i byte
  code
   loop i = 1 to 16;
      show(15, 16 + 3 * i, per:StenOpt[i], @n02)
      show(16, 16 + 3 * i, per:Pesos[i], @n02)
   .
   sethue(15,1)
   loop i = 1 to 16
      ask(15, 16 + 3 * i, per:StenOpt[i], @n02)
      ask(16, 16 + 3 * i, per:Pesos[i], @n02)
   .
   sethue

   Per:MaxDesv = 0
   Per:MaxDesv4 = 0
   loop i = 1 to 16
      ! Calculamos desviacion maxima general
      if(Per:StenOpt[i] > 5)
        Per:MaxDesv += Per:Pesos[i] * (Per:StenOpt[i] - 1) ^ 2
      else
        Per:MaxDesv += Per:Pesos[i] * (10 - Per:StenOpt[i]) ^ 2
      .
      ! Calculamos desviacion maxima de los 4 factores
      if(i <> 2 and i <> 3 and i <> 6 and i <> 10)
        cycle     ! No es ninguno de los 4 factores
      .
      if(Per:StenOpt[i] > 5)
        Per:MaxDesv4 += Per:Pesos[i] * (Per:StenOpt[i] - 1) ^ 2
      else
        Per:MaxDesv4 += Per:Pesos[i] * (10 - Per:StenOpt[i]) ^ 2
      .
   .




Mezcla Procedure
xTest        File,Pre(xxx),Create,Reclaim
Por_Nombre     Key(xxx:Nombre),Dup,Nocase,Opt
Por_Desviac    Key(xxx:Desviacion),Dup,Nocase
Por_cedula     Key(xxx:Ced_Nro),Nocase,Opt
Por_Espec      Key(xxx:Espec_postul,xxx:Ced_Nro),Dup,Nocase
Por_Esp_Desv   Index(xxx:Espec_postul,xxx:Desviacion),Nocase,Opt
RECORD         Record
cedula_ceap      String(12)                      !formato de cedula del ceap
Univ             String(4)                       !Universidad de la que viene
Titulo           String(30)                      !Titulo obtenido
Cedula           Group                           !Cedula
Ced_VoE            String(1)                     !Venezolano o Chileno
Ced_Nro            Long                          !Nro de Cedula
                 .
Nombre           String(40)                      !Nombre del entrevistado
L_Nacim          String(30)                      !Lugar de nacimiento
F_Nacim          Long                            !Fecha de nacimiento
F_Test           Long                            !Fecha del test
Tiempo           Group                           !Tiempo de ejecucion del test
Horas              Byte
Minutos            Byte
                 .
Notas            Real                            !Promedio de notas
Escala           Real                            !Escala de notas usada
Promocion        Short                           !Lugar en su promocion
Total_Promoc     Short                           !Total de pers. en la promocion
Anos_Trabajo     Byte                            !A§os de trabajo
Anos_Trab_A      Byte                            !A§os de trabajo en el area
Gra_Instr        Byte                            !Grado de instruccion
Cargo            Byte                            !Cargo que desempe§a
Sexo             String(1)
Edad             Real                            !Edad en formato "standard"
Anos             Byte                            !A§os de edad
Meses            Byte                            !Meses de edad
Espec_Origen     String(@p##-###-##p)            !Especialidad de origen
Espec_postul     String(9)                       !Especialidad a la que postula
Respuesta        String(1),Dim(190)              !Respuestas al test
Ensayo           Byte                            !Puntaje por el ensayo ?????
Norma            Short                           !Norma usada para calificar
Perfil           Byte                            !Perfil usado para calificar
Raw              Byte,Dim(16)                    !Puntuacion cruda obtenida
Sten             Byte,Dim(16)                    !Puntuacion Estandarizada
Desviacion       Real                            !Desviacion del perfil
             . .

Mezcla  DOS, NAME('Mezcla.txt'), ascii
          Record
Reg         group
mcedula       string(size(xxx:cedula_ceap))
mnombre       string(size(xxx:nombre))
mrespuesta    string(20)
        . . .

Errores DOS, NAME('Error.txt'), ascii
          Record
Reg         group
ecedula       string(size(xxx:cedula_ceap))
enombre       string(size(xxx:nombre))
erespuesta    string(20)
        . . .

  code
     beep
     show(25, 1, 'Desactivada')
     return

     create(Mezcla)
     IF ERROR()                                     !OPEN RETURNED AN ERROR
        STOP('Mezcla: ' & ERROR())                  !  STOP EXECUTION
     .
     create(Errores)
     IF ERROR()                                     !OPEN RETURNED AN ERROR
        STOP('Errores: ' & ERROR())                 !  STOP EXECUTION
     .
     OPEN(XTest)                                    !OPEN THE FILE
     IF ERROR()                                     !OPEN RETURNED AN ERROR
       CASE ERRORCODE()                             ! CHECK FOR SPECIFIC ERROR
       OF 46                                        !  KEYS NEED TO BE REBUILT
       orof 27                                      ! nomatch_err
         SHOW(25,1,CENTER('Reconstruyendo XTest',80)) !INDICATE MSG
         BUILD(XTest)                               !  CALL THE BUILD PROCEDURE
         BLANK(25,1,1,80)                           !  BLANK THE MESSAGE
       ELSE                                         ! ANY OTHER ERROR
         LOOP;STOP('XTest: ' & ERROR()).            !  STOP EXECUTION
     . .

  set(xtest)
  loop until eof(xtest)
    next(xtest)
    if(xxx:norma = 0) then cycle.    ! No ha sido actualizado
    show(25, 2, pointer(xtest), @n04)
    show(25, 15, xxx:ced_nro, @n08)
    tes:ced_VoE = xxx:ced_VoE
    tes:ced_Nro = xxx:ced_Nro
    get(test, tes:Por_cedula)
    IF ERROR()
        STOP('Test: ' & ERROR()).
    show(25, 70, Tes:Perfil)
    show(25, 25, Tes:Nombre)
    if(tes:norma <> 0) then
      if(xxx:record <> tes:record)
    ! Reporte de error y loop
        ecedula = xxx:cedula_ceap
        enombre = xxx:nombre
        erespuesta = xxx:respuesta[1]
        add(errores)
      .
    cycle
    .
    ! Actualizar
    tes:record = xxx:record
    put(test)
    IF ERROR()                                      !OPEN RETURNED AN ERROR
      STOP('Test: ' & ERROR())                      !  STOP EXECUTION
    .
    mcedula = xxx:cedula_ceap
    mnombre = xxx:nombre
    mrespuesta = xxx:respuesta[1]
    add(mezcla)
  .
  Close(XTest)
  Close(Mezcla)
  Close(errores)




Read_Datos Procedure

Datos  DOS, NAME('todos.txt'), ascii
          Record
Reg         group
xcarrera    string(9)
xspace      string(1)
xespec      string(5)
xplanilla   string(5)
xclase      string(1)
xcedula     string(9)
xapellido   string(15)
xnombre     string(15)
xsexo       string(1)
xest_civil  string(1)
xnacion     string(1)
  !xf_nac   string(6)
xf_nacim    group !, over(xf_nac)
xdia          string(2)
xmes          string(2)
xano          string(2)
            .
xtelef      string(10)
xciudad     string(20)
xestado     string(20)
xcosteo     string(1)
xtitulo     string(5)
xano_grado  string(4)
xuniv       string(5)
xkk         string(14)
        . . .

  code
    open(datos)
    IF ERROR()
       STOP('Datos: ' & ERROR())
    .
    set(datos)
    loop until eof(datos)
      next(datos)
      show(25, 1, xcarrera)
      show(25, 11, xnombre)
      tes:ced_nro = deformat(xcedula)
      get(test, tes:por_cedula)
      IF ERROR()
 !      STOP('Test: ' & ERROR())
        cycle
      .
      show(25, 30, tes:nombre)
 !    IF(xcarrera <> tes:espec_postul)
 !      STOP('Test: ')
 !    .


      tes:f_nacim = date(deformat(xmes), deformat(xdia), deformat(xano))
      tes:espec_origen = xtitulo
      tes:univ = xuniv
      show(25, 60, tes:f_nacim, @d5)
      show(25, 70, xtitulo)

      put(test)
      IF ERROR()                                    !Put RETURNED AN ERROR
        STOP('Test: ' & ERROR())                    !  STOP EXECUTION
      .
    .
    close(datos)


Read_Tablas Procedure

Carrera DOS, NAME('\ceap\crecar.dat')
          Record
Reg         group
xcodigo     string(5)
xdescr      string(40)
xfiller     string(3)
        . . .

Univ    DOS, NAME('\ceap\creuni.dat')
          Record
Reg         group
xxcodigo     string(5)
xxdescr      string(35)
xxfiller     string(56)
        . . .

i byte

  code
    open(carrera)
    IF ERROR()
       STOP('carrera: ' & ERROR())
    .
    i = 0
    set(carrera)
    loop until eof(carrera)
      next(carrera)
      i += 1
      show(25, 1, xcodigo)
      show(25, 7, xdescr)
      show(25, 49, i)
      esp:codigo = xcodigo
      esp:descripcion = xdescr
      add(espec)
      IF ERROR()                                    !Put RETURNED AN ERROR
        STOP('Espec: ' & ERROR())                   !  STOP EXECUTION
      .
    .
    close(carrera)

    open(Univ)
    IF ERROR()
       STOP('Univ: ' & ERROR())
    .
    i = 0
    set(Univ)
    loop until eof(Univ)
      next(Univ)
      i += 1
      show(25, 1, xxcodigo)
      show(25, 7, xxdescr)
      show(25, 49, i)
      uni:codigo = xxcodigo
      uni:nombre = xxdescr
      add(universi)
      IF ERROR()                                    !Put RETURNED AN ERROR
        STOP('Universi: ' & ERROR())                !  STOP EXECUTION
      .
    .
    close(Univ)

