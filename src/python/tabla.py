# Calculador de la tabla de multiplicar
# coding=utf-8
import sys
done = False
while not done:
	try:
		x = int(input("Introduzca un n�mero del 1 al 30 o 0 para salir: "))
		if x == 0:
			sys.exit()
		elif x < 1 or x > 30:
			print("Solo numeros del 1 al 10")
		else:
			done = True
	except ValueError:
		print("Tiene que ser un n�mero")
for y in range(1, 31):
	print("%2d X %2d = %3d" % (x, y, x * y))

