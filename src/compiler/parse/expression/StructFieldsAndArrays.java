/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.parse.expression;
import compiler.Context;
import compiler.Operator;
import compiler.expression.Expression;
import compiler.expression.ExpressionArrayAccess;
import compiler.expression.ExpressionConstNum;
import compiler.expression.ExpressionOperator;
import compiler.expression.ExpressionPointerDeref;
import compiler.expression.ExpressionStructFieldAccess;
import compiler.token.Token;
import static compiler.token.Token.is;
import static compiler.token.TokenType.*;
import compiler.type.Type;
import compiler.type.TypeInt32;
import compiler.type.TypeNumerical;
import compiler.type.TypePointer;
import java.util.ArrayList;
import java.util.Optional;

/**
 *
 * @author leijurv
 */
class StructFieldsAndArrays extends TokenBased {
    StructFieldsAndArrays() {
        super(o -> o == STARTBRAKT || o == PERIOD);
    }
    @Override
    protected boolean apply(int i, ArrayList<Object> o, Optional<Type> desiredType, Context context) {
        if (o.get(i) == STARTBRAKT) {
            o.remove(i);
            int sq = 1;
            int j = i;
            ArrayList<Object> inBrkts = new ArrayList<>();
            while (j < o.size()) {
                Object ob = o.remove(j);
                if (ob == STARTBRAKT) {
                    sq++;
                    continue;
                }
                if (ob == ENDBRKT) {
                    sq--;
                    if (sq == 0) {
                        break;
                    }
                    continue;
                }
                inBrkts.add(ob);
            }
            if (sq != 0) {
                throw new IllegalStateException("Mismatch " + o);
            }
            Expression index = ExpressionParser.parseImpl(inBrkts, Optional.of(new TypeInt32()), context);//TODO should array indices be int32s? but pointers are int64s... =(
            Expression array = (Expression) o.remove(i - 1);
            TypePointer tp = (TypePointer) array.getType();
            Type arrayContents = tp.pointingTo();
            int s = arrayContents.getSizeBytes();
            if (arrayContents instanceof TypeNumerical && (s == 1 || s == 2 || s == 4 || s == 8)) {
                //array deref
                o.add(i - 1, new ExpressionArrayAccess(array, index));
                return true;
            }
            //so we want...
            //*(array + index * sizeof(arrayContents))
            Expression finalIndex;
            if (arrayContents.getSizeBytes() == 1) {
                finalIndex = index;
            } else {
                int l = arrayContents.getSizeBytes();
                if ((l & (l - 1)) == 0) {
                    int k = 0;
                    while (l > 1) {
                        l >>>= 1;
                        k++;
                    }
                    finalIndex = new ExpressionOperator(index, Operator.SHIFT_L, new ExpressionConstNum(k, new TypeInt32()));
                } else {
                    finalIndex = new ExpressionOperator(index, Operator.MULTIPLY, new ExpressionConstNum(arrayContents.getSizeBytes(), new TypeInt32()));
                }
            }
            //*(array+finalIndex)
            Expression ptr = new ExpressionOperator(array, Operator.PLUS, finalIndex);
            //*(ptr)
            Expression element = new ExpressionPointerDeref(ptr);
            o.add(i - 1, element);
            return true;
        }
        if (o.get(i) == PERIOD) {
            if (!is(o.get(i + 1), VARIABLE)) {
                throw new IllegalStateException();
            }
            String fieldName = (String) ((Token) o.remove(i + 1)).data();
            o.remove(i);
            Expression prev = (Expression) o.remove(i - 1);
            if (prev.getType() instanceof TypePointer) {
                //System.out.println(prev + " " + prev.getType() + " " + new ExpressionPointerDeref(prev) + " " + new ExpressionPointerDeref(prev).getType());
                prev = new ExpressionPointerDeref(prev);//allow things like a.b=1 if a is a pointer to a struct
            }
            o.add(i - 1, new ExpressionStructFieldAccess(prev, fieldName));
            return true;
        }
        throw new IllegalStateException();
    }
}
