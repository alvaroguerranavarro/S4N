package co.com.s4n.training.java.vavr;

import io.vavr.Lazy;
import io.vavr.concurrent.Future;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LazySuite {

    @Test
    public void testLazy()
    {

        Lazy<Future<Integer>> f1 = Lazy.of(() -> Future.of(()->{
            Thread.sleep(500);
            return 5 + 4;
        }));

        Lazy<Future<Integer>> f2 = Lazy.of(() -> Future.of(()->{
            Thread.sleep(800);
            return 5 + 4;
        }));

        Lazy<Future<Integer>> f3 = Lazy.of(() -> Future.of(()->{
            Thread.sleep(300);
            return 5 + 4;
        }));
        long iniciolazy = System.nanoTime();
        Future<Integer> f4 = f1.get().flatMap(a -> f2.get()
                .flatMap(b -> f3.get().
                        flatMap(c -> Future.of(() -> a + b + c))));

        f4.await();
        long final1 = System.nanoTime();

        long resul = TimeUnit.MILLISECONDS.convert(final1-iniciolazy,TimeUnit.NANOSECONDS);

        System.out.println(resul);

        assertTrue(resul > 1600);

    }


    @Test

    public void testLazySuppier()
    {
        Supplier<Integer> s1 = () -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 4;
        };

        Lazy<Integer> f = Lazy.of(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 5;
        });

        long inicios = System.nanoTime();
        s1.get();
        long finals = System.nanoTime();
        long resuls = TimeUnit.MILLISECONDS.convert(finals-inicios,TimeUnit.NANOSECONDS);

        long iniciolaz = System.nanoTime();
        f.get();
        long finalslaz = System.nanoTime();
        long resullaz = TimeUnit.MILLISECONDS.convert(finalslaz-iniciolaz,TimeUnit.NANOSECONDS);



        long inicios2 = System.nanoTime();
        s1.get();
        long finals2 = System.nanoTime();
        long resuls2 = TimeUnit.MILLISECONDS.convert(finals2-inicios2,TimeUnit.NANOSECONDS);

        long iniciolaz2 = System.nanoTime();
        f.get();
        long finalslaz2 = System.nanoTime();
        long resullaz2 = TimeUnit.MILLISECONDS.convert(finalslaz2-iniciolaz2,TimeUnit.NANOSECONDS);

        System.out.println(resuls);
        System.out.println(resullaz);
        System.out.println(resuls2);
        System.out.println(resullaz2);

        assertEquals(resuls,resuls2);
    }

}

