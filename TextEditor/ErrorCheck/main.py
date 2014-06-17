# -*- coding: utf-8 -*-

import sys
import ply.yacc as yacc
import fileinput
from Cparser import Cparser
from TypeChecker import TypeChecker

if __name__ == '__main__':

#     try:
#          filename = sys.argv[1] if len(sys.argv) > 1 else "example.txt"
#          file = open(filename, "r")
#     except IOError:
#          print("Cannot open {0} file".format(filename))
#          sys.exit(0)
#     text = file.read()

     text = ""

     for line in fileinput.input():
		text += line
     
     Cparser = Cparser()
     checker = TypeChecker()
     parser = yacc.yacc(module=Cparser)
     tree = parser.parse(text,lexer=Cparser.scanner)
     
     if Cparser.error:
     	  print "Blad parsowania! Nie mozna wyswietlic drzewa."
     else:
          if tree.accept(checker):
               print "Operacja zakończona powodzeniem."
          else:
               print "Wykryto błędy."
