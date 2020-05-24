class Terrestre:
	def desplazar(self):
		print("El animal anda")

class Acuatico:
	def desplazar(self):
		print("El animal nada")

class Cocodrilo(Terrestre, Acuatico):
	pass

c = Cocodrilo()

c.desplazar()