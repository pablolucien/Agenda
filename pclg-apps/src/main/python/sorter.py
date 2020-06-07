# Ordena el fichero de gastos de Sopra
# coding=latin-1
import datetime
import re  # Regular Expresions
import sys
import time


def comparator(o1, o2):
	m1 = re.findall(r'[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}', o1)[0]
	m2 = re.findall(r'[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}', o2)[0]
	d1 = time.strptime(m1, "%d/%m/%Y")
	d2 = time.strptime(m2, "%d/%m/%Y")
	if d1 < d2:
		return -1
	elif d2 < d1:
		return 1
	return 0

def normalizeDates(list):
	for ii in range(len(list)):
		line = list[ii]
		m1 = re.findall(r'[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}', line)[0]
		d1 = datetime.datetime(*time.strptime(m1, "%d/%m/%Y")[0:5])
		s1 = d1.strftime("%d/%m/%Y")
		list[ii] = line.replace(m1, s1)

def sorter(filename):
	input = open(filename, 'r')
	output = open('sorted_' + filename, 'w')
	list = input.readlines()
	dietasList = []
	ticketList = []
	for line in list:
		if line.startswith("Dieta"):
			dietasList.append(line)
		elif line.startswith("Ticket"):
			ticketList.append(line)
		elif line.startswith("Detalle"):
			output.write(line)
		else:
			print('Invalid data:', line

	dietasList.sort(comparator)
	ticketList.sort(comparator)

	normalizeDates(dietasList)
	normalizeDates(ticketList)

	output.writelines(dietasList)
	output.writelines(ticketList)

if __name__ == '__main__':
	if len(sys.argv) != 2:
		print("Uso: python ExpensesSorter <archivo>")
		exit(1)

	sorter(sys.argv[1])