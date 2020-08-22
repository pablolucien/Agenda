"""Everyone knows that debugging is twice as hard as writing a program in the
first place. So if you're as clever as you can be when you write it,
how will you ever debug it?

Brian Kernighan, The Elements of Programming Style, 2nd edition, chapter"""

__version__ = "$Revision: 1.3 $"
# $Source: /home/cvs-repository/modules/src/python/desayunos.py,v $

# Calculador de los precios de los desayunos
# coding=utf-8

from sys import exit


def __getQuantity():
    """Obtiene la cantidad de comensales"""
    while True:
        try:
            numPersonas = int(input("Enter the number of people (0 to exit): "))
            if numPersonas == 0:
                print("That's all folks!")
                exit()
            elif numPersonas < 0:
                print("It must be a non-negative number, grasshopper")
            else:
                return numPersonas
        except ValueError:
            print("It must be a number")


def printLine(y):
    print("\t%10d |\t\t\t\t\t\t %5.2f  |\t\t %5.2f" % (y, 2.3 * (y - 1) + 3.2, 2.3 * y))
    print('\t' + '-' * 58)


def compute():
    """Realiza el calculo"""
    numPersonas = __getQuantity()

    print("""
    ==========================================================
    Calculador automatico de precios de desayuno. Version 2.0
    ==========================================================
    N Personas |\t(N - 1) Barritas + 1 Pincho | \tN Barritas
    ==========================================================""")

    # map(printLine, range(1, numPersonas + 1))
    for x in range(1, numPersonas + 1):
        printLine(x)


if __name__ == '__main__':
    compute()
