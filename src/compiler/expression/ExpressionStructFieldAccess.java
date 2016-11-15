/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.expression;
import compiler.Context;
import compiler.Context.VarInfo;
import compiler.Operator;
import compiler.Struct;
import compiler.command.Command;
import compiler.command.CommandSetPtr;
import compiler.tac.IREmitter;
import compiler.tac.TACConst;
import compiler.tac.TempVarUsage;
import compiler.type.Type;
import compiler.type.TypePointer;
import compiler.type.TypeStruct;
import java.awt.image.RasterFormatException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.nio.channels.ShutdownChannelGroupException;
import java.nio.file.ReadOnlyFileSystemException;

/**
 *
 * @author leijurv
 */
public class ExpressionStructFieldAccess extends Expression implements Settable {
    String field;
    Expression input;
    Struct struct;
    public ExpressionStructFieldAccess(Expression input, String field) {
        this.struct = ((TypeStruct) input.getType()).struct;
        this.input = input;
        this.field = field;
        if (struct.getFieldByName(field) == null) {
            throw new ShutdownChannelGroupException();
        }
    }
    @Override
    protected Type calcType() {
        return struct.getFieldByName(field).getType();
    }
    @Override
    public void generateTAC(IREmitter emit, TempVarUsage tempVars, String resultLocation) {
        String temp = tempVars.getTempVar(input.getType());
        VarInfo structLocation = tempVars.getInfo(temp);
        input.generateTAC(emit, tempVars, temp);
        int structLocationOnStack = structLocation.getStackLocation();
        int offsetOfThisFieldWithinStruct = struct.getFieldByName(field).getStackLocation();
        int fieldLocationOnStack = structLocationOnStack + offsetOfThisFieldWithinStruct;
        System.out.println(structLocationOnStack + " " + offsetOfThisFieldWithinStruct + " " + fieldLocationOnStack + " " + struct.getFieldByName(field));
        String fieldLabel = tempVars.registerLabelManually(fieldLocationOnStack, struct.getFieldByName(field).getType());
        emit.emit(new TACConst(resultLocation, fieldLabel));
    }
    @Override
    protected int calculateTACLength() {
        return input.getTACLength() + 1;
    }
    @Override
    public Expression calculateConstants() {
        input = input.calculateConstants();
        return this;
    }
    @Override
    public Expression insertKnownValues(Context context) {
        input = input.insertKnownValues(context);
        return this;
    }
    @Override
    public Command setValue(Expression rvalue, Context context) {
        int offsetOfThisFieldWithinStruct = struct.getFieldByName(field).getStackLocation();
        if (input instanceof ExpressionPointerDeref) {
            //input.field=rvalue
            //(*deref).field=rvalue
            Expression deref = ((ExpressionPointerDeref) input).deReferencing;
            //*(deref + offsetOfThisFieldWithinStruct) = rvalue
            Expression fieldLoc = new ExpressionOperator(deref, Operator.PLUS, new ExpressionConstNum(offsetOfThisFieldWithinStruct));
            fieldLoc = new ExpressionCast(fieldLoc, new TypePointer<>(struct.getFieldByName(field).getType()));
            //*(fieldLoc) = rvalue
            System.out.println("bcdua " + struct + " " + field + " " + fieldLoc + " " + deref + " " + rvalue);
            if (!(rvalue.getType().equals(((TypePointer) fieldLoc.getType()).pointingTo()))) {
                throw new RasterFormatException(rvalue.getType() + " " + fieldLoc.getType());
            }
            return new CommandSetPtr(context, fieldLoc, rvalue);
        } else if (input instanceof ExpressionVariable) {
            //input.field=rvalue
            VarInfo thisVariable = context.get(((ExpressionVariable) input).name);
            return new CommandSetStructField(context, thisVariable.getStackLocation() + offsetOfThisFieldWithinStruct, rvalue);
        } else {
            throw new UnsupportedOperationException(input + " ", new MalformedParameterizedTypeException());
        }
    }

    private class CommandSetStructField extends Command {
        Expression insert;
        int stackLoc;
        public CommandSetStructField(Context aids, int stackLoc, Expression insert) {
            super(aids);
            this.stackLoc = stackLoc;
            this.insert = insert;
            if (!insert.getType().equals(struct.getFieldByName(field).getType())) {
                throw new ReadOnlyFileSystemException();
            }
        }
        @Override
        protected void generateTAC0(IREmitter emit) {
            TempVarUsage cancer = new TempVarUsage(context);
            String tam = cancer.getTempVar(insert.getType());
            insert.generateTAC(emit, cancer, tam);
            String thisField = cancer.registerLabelManually(stackLoc, struct.getFieldByName(field).getType());
            emit.emit(new TACConst(thisField, tam));
        }
        @Override
        protected int calculateTACLength() {
            return insert.getTACLength() + 1;
        }
        @Override
        public void staticValues() {
        }
    }
}