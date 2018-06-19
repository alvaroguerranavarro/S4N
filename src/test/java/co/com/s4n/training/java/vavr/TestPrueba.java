package co.com.s4n.training.java.vavr;

import co.com.s4n.training.java.jdk.Prueba;
import io.vavr.control.Try;
import org.junit.Test;


import static io.vavr.API.For;
import static io.vavr.API.Success;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestPrueba {
    @Test
    public void testSucessWithFlatMap()
    {
        Try<Integer> res = Prueba.compararTry(3,2)
                .flatMap(r0->Prueba.postivoTry(r0))
                .flatMap(r1->Prueba.multiplicarTry(r1,r1))
                .flatMap(r2 -> Prueba.divisionTry(r2,r2));
        assertEquals(Success(1),res);
    }

    @Test
    public void testFailureWithFlatMap()
    {
        Try<Integer> res = Prueba.compararTry(3,2)
                .flatMap(r0->Prueba.postivoTry(r0))
                .flatMap(r1->Prueba.multiplicarTry(r1,r1))
                .flatMap(r2 -> Prueba.divisionTry(r2,0));
        assertTrue(res.isFailure());
    }

    @Test
    public void tesRecoverFlatMap()
    {
        Try<Integer> res = Prueba.compararTry(3,2)
                .flatMap(r0->Prueba.postivoTry(r0))
                .flatMap(r1->Prueba.multiplicarTry(r1,r1))
                .flatMap(r2 -> Prueba.divisionTry(r2,0).recover(Exception.class,2));
        assertEquals(Success(2),res);
    }

    @Test
    public void tesRecoverWithFlatMap()
    {
        Try<Integer> res = Prueba.compararTry(3,2)
                .flatMap(r0->Prueba.postivoTry(r0))
                .flatMap(r1->Prueba.multiplicarTry(r1,r1))
                .flatMap(r2 -> Prueba.divisionTry(r2,0).recoverWith(Exception.class,Try.of(() ->  1)));
        assertEquals(Success(1),res);
    }

    @Test
    public void tesRecoverWithFor()
    {
        Try<Integer> res =
                For(Prueba.compararTry(3,3),r0 ->
                        For(Prueba.postivoTry(r0),r1 ->
                            For(Prueba.multiplicarTry(r1,r1),r2->
                                 Prueba.divisionTry(r2,0).recoverWith(Exception.class,Try.of(()-> 1))))).toTry();
        assertEquals(Success(1),res);
    }

    @Test
    public void tesRecoverFor()
    {
        Try<Integer> res =
                For(Prueba.compararTry(3,3),r0 ->
                        For(Prueba.postivoTry(r0),r1 ->
                                For(Prueba.multiplicarTry(r1,r1),r2->
                                        Prueba.divisionTry(r2,0).recover(Exception.class,1)))).toTry();
        assertEquals(Success(1),res);
    }

}
