# -*- coding: utf-8 -*-

class OperationsTable:

    def __init__(self):
        self.type = {}
        self.addOperation('+','int','float','float')
        self.addOperation('-','int','float','float')
        self.addOperation('*','int','float','float')
        self.addOperation('/','int','float','float')
        self.addOperation('+','float','int','float')
        self.addOperation('-','float','int','float')
        self.addOperation('*','float','int','float')
        self.addOperation('/','float','int','float')
        self.addOperation('+','float','float','float')
        self.addOperation('-','float','float','float')
        self.addOperation('*','float','float','float')
        self.addOperation('/','float','float','float')
        self.addOperation('+','int','int','int')
        self.addOperation('-','int','int','int')
        self.addOperation('*','int','int','int')
        self.addOperation('/','int','int','int')
        self.addOperation('%','int','int','int')
        self.addOperation('==','float','float','int')
        self.addOperation('!=','float','float','int')
        self.addOperation('<','float','float','int')
        self.addOperation('>','float','float','int')
        self.addOperation('<=','float','float','int')
        self.addOperation('>=','float','float','int')
        self.addOperation('==','int','int','int')
        self.addOperation('!=','int','int','int')
        self.addOperation('<','int','int','int')
        self.addOperation('>','int','int','int')
        self.addOperation('<=','int','int','int')
        self.addOperation('>=','int','int','int')
        self.addOperation('==','string','string','int')
        self.addOperation('!=','string','string','int')
        self.addOperation('<','string','string','int')
        self.addOperation('>','string','string','int')
        self.addOperation('*','string','int','string')

    def addOperation(self, op, left, right, result):
        if not op in self.type: self.type[op] = {}
        if not left in self.type[op]: self.type[op][left] = {}
        if not right in self.type[op][left]: 
            self.type[op][left][right] = result
        else: 
            raise Error("już istnieje")

    def getOperationType(self, op, left, right):
        if op not in self.type: return None
        if left not in self.type[op]: return None
        if right not in self.type[op][left]: return None
        return self.type[op][left][right]
