/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.util;
import java.util.Objects;

/**
 *
 * @author leijurv
 * @param <A>
 * @param <B>
 */
public class Pair<A, B> {
    private final A a;
    private final B b;
    public A getKey() {
        return a;
    }
    public B getValue() {
        return b;
    }
    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }
    @Override
    public String toString() {
        return a + "=" + b;
    }
    @Override
    public boolean equals(Object o) {
        return this == o || (o != null && o.getClass() == getClass() && hashCode() == o.hashCode());
    }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.a);
        hash = 67 * hash + Objects.hashCode(this.b);
        return hash;
    }
}