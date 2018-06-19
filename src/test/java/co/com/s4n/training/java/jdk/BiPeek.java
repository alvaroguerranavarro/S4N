package co.com.s4n.training.java.jdk;

import io.vavr.control.Either;

import java.util.function.Consumer;

public class BiPeek<A,B>
{

    public Either execute(Consumer<A> pl, Consumer<B> pr, Either<A,B> e)
    {
        return e.isRight()? e.peek(pr) : e.peekLeft(pl);
    }
}