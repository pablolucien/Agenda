# Validador de DNI
# coding=latin-1
letra = "TRWAGMYFPDXBNJZSQVHLCKE"
dni = int(input("Introduzca el número del DNI: "))
print("La letra correspondiente es:", letra[dni % 23])
