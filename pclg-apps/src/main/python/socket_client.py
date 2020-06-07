import socket

s = socket.socket()
s.connect(("localhost", 9999))
while True:
	mensaje = input("> ")
	s.send(bytes(mensaje, 'UTF-8'))
	if mensaje == "quit":
		break
print("adios")
s.close()