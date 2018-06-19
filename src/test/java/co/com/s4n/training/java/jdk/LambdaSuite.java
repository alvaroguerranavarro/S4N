package co.com.s4n.training.java.jdk;

import static org.junit.Assert.*;

import io.vavr.collection.List;
import org.junit.Test;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;

public class LambdaSuite {

    @FunctionalInterface
    interface InterfaceDeEjemplo {
        int metodoDeEjemplo(int x, int y);
    }

    class ClaseDeEjemplo {
        public int metodoDeEjemplo1(int z, InterfaceDeEjemplo i) {
            return z + i.metodoDeEjemplo(1, 2);
        }

        public int metodoDeEjemplo2(int z, BiFunction<Integer, Integer, Integer> fn) {
            return z + fn.apply(1, 2);
        }
    }

    @Test
    public void smokeTest() {
        assertTrue(true);
    }

    @Test
    public void usarUnaInterfaceFuncional1() {

        InterfaceDeEjemplo i = (x, y) -> x + y;

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo1(1, i);

        assertTrue(resultado == 4);
    }

    @Test
    public void usarUnaInterfaceFuncional3() {

        InterfaceDeEjemplo i = (x, y) -> x * y;

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo1(1, i);

        assertTrue(resultado == 3);
    }

    @Test
    public void usarUnaInterfaceFuncional2() {

        BiFunction<Integer, Integer, Integer> f = (x, y) -> new Integer(x.intValue() + y.intValue());

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo2(1, f);

        assertTrue(resultado == 4);
    }

    @Test
    public void usarUnaInterfaceFuncional4() {

        BiFunction<Integer, Integer, Integer> f = (x, y) -> new Integer(x.intValue() + y.intValue());

        ClaseDeEjemplo instancia = new ClaseDeEjemplo();

        int resultado = instancia.metodoDeEjemplo2(3, f);

        assertTrue(resultado == 6);
    }

    class ClaseDeEjemplo2 {

        public int metodoDeEjemplo2(int x, int y, IntBinaryOperator fn) {
            return fn.applyAsInt(x, y);
        }
    }

    @Test
    public void usarUnaFuncionConTiposPrimitivos() {
        IntBinaryOperator f = (x, y) -> x + y;

        ClaseDeEjemplo2 instancia = new ClaseDeEjemplo2();

        int resultado = instancia.metodoDeEjemplo2(1, 2, f);

        assertEquals(3, resultado);
    }

    @Test
    public void usarUnaFuncionConTiposPrimitivos1() {
        IntBinaryOperator f = (x, y) -> x + y;

        ClaseDeEjemplo2 instancia = new ClaseDeEjemplo2();

        int resultado = instancia.metodoDeEjemplo2(1, 2, f);

        assertEquals(3, resultado);
    }

    class ClaseDeEjemplo3 {

        public String operarConSupplier(Supplier<Integer> s) {
            return "El int que me han entregado es: " + s.get();
        }
    }

    @Test
    public void usarUnaFuncionConSupplier() {
        Supplier s1 = () -> {
            System.out.println("Cuándo se evalúa esto? (1)");
            return 4;
        };

        Supplier s2 = () -> {
            System.out.println("Cuándo se evalúa esto? (2)");
            return 4;
        };

        ClaseDeEjemplo3 instancia = new ClaseDeEjemplo3();

        String resultado = instancia.operarConSupplier(s1);

        assertEquals("El int que me han entregado es: 4", resultado);
    }

    class ClaseDeEjemplo4 {

        private int i = 0;

        public void operarConConsumer(Consumer<Integer> c) {
            c.accept(i);
        }
    }

    class ClaseDeEjemplo5 {

        public void operarConConsumer(int i, Consumer<Integer> c) {
            c.accept(i);
        }
    }

    @Test
    public void usarUnaFuncionConConsumer() {
        Consumer<Integer> c1 = x -> {
            System.out.println("Me han entregado este valor: " + x);
        };

        ClaseDeEjemplo5 instancia = new ClaseDeEjemplo5();

        instancia.operarConConsumer(5, c1);

    }

    @FunctionalInterface
    interface EjercicioInterface {
        public Consumer<Integer> sam(Supplier<Integer> s1,
                                     Supplier<Integer> s2,
                                     Supplier<Integer> s3
        );
    }


    @Test
    public void t() {
        EjercicioInterface i = (x, y, z) -> {
            Integer partial = x.get() + y.get() + z.get();
            Consumer<Integer> c = n -> {
                Integer suma = partial.intValue() + n;
                System.out.println("Consumer ---->>> " + (partial + n));
                System.out.println("Consumer ---->>> " + suma);
            };

            return c;

        };

        Supplier x = () -> 1;
        Supplier y = () -> 2;
        Supplier z = () -> 3;

        Consumer<Integer> consumer = i.sam(x, y, z);
        consumer.accept(new Integer(9));
    }

    // New Test

    @Test
    public void Lambda() {
        EjercicioInterface i = (x, y, z) -> {
            Integer partial = x.get() * y.get() * z.get();
            Consumer<Integer> c = n -> {
                Integer multip = partial.intValue() * n;
                System.out.println("Consumer ---->>> " + (partial * n));
                System.out.println("Consumer ---->>> " + multip);
                assertEquals(18,multip.intValue());
            };

            return c;

        };

        Supplier x = () -> 1;
        Supplier y = () -> 2;
        Supplier z = () -> 3;

        Consumer<Integer> consumer = i.sam(x, y, z);
        consumer.accept(new Integer(3));
    }


    class ClaseDeEjemplo1 {
        public int metodoDeEjemplo1(int z, InterfaceDeEjemplo i) {
            return z * i.metodoDeEjemplo(5, 5);
        }
    }


        @Test
        public void usarUnaInterfaceFuncional7() {

            InterfaceDeEjemplo i = (x, y) -> x * y;

            ClaseDeEjemplo1 instancia = new ClaseDeEjemplo1();

            int resultado = instancia.metodoDeEjemplo1(2, i);

            assertEquals(50,resultado);
        }
}
