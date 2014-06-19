# -*- coding: utf-8 -*-

from AST import *
from SymbolTable import *
from OperationsTable import OperationsTable as OT

class TypeChecker:

    def __init__(self):
        self.symbolTable = None
        self.operationsTable = OT()
        self.errors = 0

    def raiseError(self, body):
        self.errors += 1
        print body

    def acceptOrVar(self, node):
        if node.__class__.__name__ == 'str':
            variable = self.symbolTable.get(node)
            return None if variable is None else variable.type
        else:
            return node.accept(self)

    @staticmethod
    def typesCoherent(should, beType):
        if __debug__: print "should",should," | ","beType", beType
        return beType == should or (beType == 'float' and should == 'int')
		
    def analyzeDeclarations(self, declarationsList):
        if __debug__: print "analyzing Declarations..."
        if declarationsList is None:
            if __debug__: print "no declarations to analyze"
            return
        for varList in declarationsList:
            if varList.accept(self):
                Type = varList.typ
                for var in varList.variables:
                    try:
                        self.symbolTable.put(var.left, VariableSymbol(var.left, Type, var))
                    except Exception:
                        self.raiseError("(linia "+str(var.lineno)+"): Podwójna deklaracja \""+var.left+"\", poprzednia deklaracja w linii "+str(self.symbolTable.get(var.left).lineno())+".")

    def analyzeFunDeclarations(self, declarationsList):
        if __debug__: print "analyzing FunDeclarations..."
        for funDef in declarationsList:
            self.symbolTable.put(funDef.ident, FunctionSymbol(funDef.ident, funDef.typ, funDef.args, funDef))
            if funDef.accept(self) is None:
                self.raiseError("(linia "+str(var.lineno)+"): B³êdy w definicji funkcji "+funDef.ident+".")

    def analyzeInstrBlock(self, block):
        if __debug__: print "analyzing InstrBlock..."
        if block.__class__.__name__ == 'CompoundInstruction':
            self.visit_CompoundInstruction(block)
        elif block.__class__.__name__ == 'list':
            self.analyzeInstr(block)
        elif isinstance(block,Node):
            block.accept(self)
        else:
            raise Exception("jakie jeszcze bloki instrukcji tu wejd¹?")

    def analyzeInstr(self, instructionsList):
        if __debug__: print "analyzing Instr..."
        for instr in instructionsList:
            instr.accept(self)

    def analyzeCondExpr(self, expr):
        if __debug__: print "analyzing Declarations..."
        condType = expr.accept(self)
        if condType != 'int':
            self.raiseError("(linia "+str(expr.lineno)+"): Nieprawid³owy typ wyra¿enia warunkowego.")
    
    def visit_BinExpr(self, node):
        if __debug__: print "visiting BinExpr in line",node.lineno
        leftType = self.acceptOrVar(node.left)
        rightType = self.acceptOrVar(node.right)
        op = node.op;
        if leftType is None:
            self.raiseError("(linia "+str(node.lineno)+"): Nie rozpoznano typu lewej strony.")
        if rightType is None:
            self.raiseError("(linia "+str(node.lineno)+"): Nie rozpoznano typu prawej strony.")
        if rightType is not None and leftType is not None:
            Type = self.operationsTable.getOperationType(op,leftType,rightType)
            if Type is None:
                self.raiseError("(linia "+str(node.lineno)+"): Nieobs³ugiwana operacja " +leftType+" "+op+" "+rightType+".")
            return Type


    def visit_Assignment(self, assInstr):
        if __debug__: print "visit_Assignment in line",assInstr.lineno
        if assInstr.left.__class__.__name__ != 'str':
            self.raiseError("(linia "+str(assInstr.lineno)+"): Lewa strona przypisania musi byæ identyfikatorem.")
        leftType = self.symbolTable.get(assInstr.left).type
        rightType = self.acceptOrVar(assInstr.right)
        if leftType is None:
            self.raiseError("(linia "+str(assInstr.lineno)+"): U¿yto niezadeklarowanej nazwy "+assInstr.left+".")
        if rightType is None:
            self.raiseError("(linia "+str(assInstr.lineno)+"): Nie rozpoznano typu wartoœci przypisywanej.")
        if rightType is not None and leftType is not None:
            if not TypeChecker.typesCoherent(should=rightType,beType=leftType):
                self.raiseError("(linia "+str(assInstr.lineno)+"): Nieprawid³owy typ wartoœci przypisywanej, wymagany "+leftType+", znaleziony "+rightType+".")


    def visit_CompoundInstruction(self, compInstr):
        if __debug__: print "visit_CompoundInstruction in line",compInstr.lineno
        prevTable = self.symbolTable
        self.symbolTable = SymbolTable(prevTable,"compInstr("+str(compInstr.lineno)+") scope")
        self.analyzeDeclarations(compInstr.decl)
        self.analyzeInstrBlock(compInstr.instr)
        self.symbolTable = prevTable
        return 'void'

    def visit_WhileLoop(self, whLoop):
        if __debug__: print "visit_WhileLoop in line",whLoop.lineno
        self.analyzeCondExpr(whLoop.cond)
        whLoop.instr.accept(self)

    def visit_RepeatUntilLoop(self, repeatLoop):
        if __debug__: print "visit_RepeatUntilLoop in line",repeatLoop.lineno
        self.analyzeInstrBlock(repeatLoop.instr)
        self.analyzeCondExpr(repeatLoop.cond)

    def visit_RelExpr(self, node):
        if __debug__: print "visit_RelExpr in line",node.lineno
        type1 = self.acceptOrVar(node.left)
        type2 = self.acceptOrVar(node.right)
        op = node.op 
        if type1 is None:
            self.raiseError("(linia "+str(node.lineno)+"): Typ wyra¿enia po lewej nie rozpoznawalny.")
        if type2 is None:
            self.raiseError("(linia "+str(node.lineno)+"): Typ wyra¿enia po prawej nie rozpoznawalny.")
        return self.operationsTable.getOperationType(op,type1,type2)

    def visit_Program(self, program):
        if __debug__: print "visit_Program in line",program.lineno
        self.symbolTable = SymbolTable(None,"program scope")
        
        self.analyzeDeclarations(program.decl)
        self.analyzeFunDeclarations(program.fundef)
        self.analyzeInstrBlock(program.instr)

        #if __debug__: 
        print "symbolTable: ", self.symbolTable.currentScope
        return self.errors is 0
		
    def visit_Variable(self, variable):
        if __debug__: print "visit_Variable in line",variable.lineno
        Type = variable.typ
        result = True
        for var in variable.variables:
            if var.right is not None:
                valueType = var.right.accept(self)
                if not TypeChecker.typesCoherent(should=valueType,beType=Type):
                    self.raiseError(str(var.lineno)+":Niekompatybilna inicjacja zmiennej "+var.left+", wymagany "+Type+", znaleziony "+valueType)
                    result = False
        return result

    def visit_Float(self, var):
        if __debug__: print "visit_Float in line",var.lineno
        return 'float'

    def visit_Integer(self, var):
        if __debug__: print "visit_Integer in line",var.lineno
        return 'int'

    def visit_String(self, var):
        if __debug__: print "visit_String in line",var.lineno
        return 'string'


    def visit_Print(self, instr):
        if __debug__: print "visit_Print in line",instr.lineno
        Type = self.acceptOrVar(instr.expr)
        if Type is None:
            self.raiseError("(linia "+str(instr.lineno)+"): Typ argumentu nie rozpoznawalny.")

    def visit_Argument(self, arg):
        if __debug__: print "visit_Argument in line",arg.lineno
        return arg.typ

    def visit_FunctionCall(self, FunctionCall):
        if __debug__: print "visit_FunctionCall in line",FunctionCall.lineno
        funDef = self.symbolTable.get(FunctionCall.ident)
        if funDef is None:
            self.raiseError("(linia "+str(FunctionCall.lineno)+"): Funkcja "+FunctionCall.ident+" nie istnieje.")
            return None

        for left, right in zip(funDef.args,FunctionCall.args):
            defType = left.accept(self)
            callType = self.acceptOrVar(right)
            if not TypeChecker.typesCoherent(should=callType, beType=defType):
                self.raiseError("(linia "+str(FunctionCall.lineno)+"): Niew³aœciwy typ parametru, wymagany "+defType+", znaleziony "+callType+".")
        return funDef.type
            

    def visit_ChoiceInstruction(self, instr):
        if __debug__: print "visit_ChoiceInstruction in line",instr.lineno
        self.analyzeCondExpr(instr.cond)
        self.analyzeInstrBlock(instr.instr)
        
    def visit_ChoiceInstructionWithElse(self, instr):
        if __debug__: print "visit_ChoiceInstructionWithElse in line",instr.lineno
        self.visit_ChoiceInstruction(instr)
        self.analyzeInstrBlock(instr.elinstr)

    def visit_FunctionDefinition(self, funDef):
        if __debug__: print "visit_FunctionDefinition in line",funDef.lineno
        prevTable = self.symbolTable
        self.symbolTable = SymbolTable(prevTable, "fundef("+funDef.ident+") scope")
        
        if funDef.args is not None:
             for arg in funDef.args:
                 self.symbolTable.put(arg.ident, VariableSymbol(arg.ident, arg.typ, arg))
        self.analyzeInstrBlock(funDef.instr)

        self.symbolTable = prevTable

        return funDef.typ

    def visit_Return(self, ret):
        if __debug__: print "visit_Return in line",ret.lineno
        return self.acceptOrVar(ret.value)
		
		
		
