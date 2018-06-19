package co.com.s4n.training.java.jdk;

import static org.junit.Assert.*;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class CompletableFutureSuite {

    private void sleep(int milliseconds){
        try{
            Thread.sleep(milliseconds);
        }catch(Exception e){
            System.out.println("Problemas durmiendo hilo");
        }
    }

    public void imprimirMensaje(String mensaje)
    {
        Date myDate = new Date();

        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S ").format(myDate) + mensaje );
    }

    @Test
    public void t1() {

        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();


        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> {
            Thread.sleep(300);

            completableFuture.complete("Hello");
            return null;
        });
            System.out.println(Thread.currentThread().getName());

        try {
            String s = completableFuture.get(500, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){
            assertTrue(false);
        }finally{
            executorService.shutdown();

        }

    }

    @Test
    public void t2(){
        CompletableFuture<String> completableFuture
                = new CompletableFuture<>();

        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> {
            Thread.sleep(300);

            completableFuture.complete("Hello");
            return null;
        });

        try {
            String s = completableFuture.get(500, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){
            assertTrue(false);
        }finally{
            executorService.shutdown();
        }
    }

    @Test
    public void t3(){
        // Se puede construir un CompletableFuture a partir de una lambda Supplier (que no recibe parámetros pero sí tiene retorno)
        // con supplyAsync
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "Hello";
        });

        try {
            String s = future.get(350, TimeUnit.MILLISECONDS);
            assertEquals(s, "Hello");
        }catch(Exception e){

            assertTrue(false);
        }
    }

    @Test
    public void t4(){

        int i = 0;
        // Se puede construir un CompletableFuture a partir de una lambda (Supplier)
        // con runAsync
        Runnable r = () -> {
            sleep(300);
            System.out.println("Soy impuro y no merezco existir");
        };

        // Note el tipo de retorno de runAsync. Siempre es un CompletableFuture<Void> asi que
        // no tenemos manera de determinar el retorno al completar el computo
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(r);

        try {
            voidCompletableFuture.get(500, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void t5(){

        String testName = "t5";

        System.out.println(testName + " - El test (hilo ppal) esta corriendo en: "+Thread.currentThread().getName());

        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> {
            System.out.println(testName + " - completbleFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        //thenApply acepta lambdas de aridad 1 con retorno
        CompletableFuture<String> future = completableFuture
                .thenApply(s -> {
                    imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
                    sleep(500);
                    return s + " World";
                })
                .thenApply(s -> {
                    imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());

                    return s + "!";
                });

        try {
            assertEquals("Hello World!", future.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }


    @Test
    public void t6(){

        String testName = "t6";

        CompletableFuture<String> completableFuture
                = CompletableFuture.supplyAsync(() -> {
            System.out.println(testName + " - completbleFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        // thenAccept solo acepta Consumer (lambdas de aridad 1 que no tienen retorno)
        // analice el segundo thenAccept ¿Tiene sentido?
        CompletableFuture<Void> future = completableFuture
                .thenAccept(s -> {
                    //System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                    sleep(500);
                })
                .thenAccept(s -> {
                    //System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName() + " lo que viene del futuro es: "+s);
                });

    }

    @Test
    public void t7(){
        String testName = "t7";

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(testName + " - completbleFuture corriendo en el thread: "+Thread.currentThread().getName());
            return "Hello";
        });

        //thenAccept solo acepta Consumer (lambdas de aridad 1 que no tienen retorno)

        CompletableFuture<Void> future = completableFuture
                .thenRun(() -> {
                    //System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    sleep(500);
                })
                .thenRun(() -> {
                    imprimirMensaje(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());

                });

    }

    @Test
    public void t8(){

        String testName = "t8";

        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    return "Hello";
                })
                .thenCompose(s -> {
                    // s es el valor  que nos entrega el futuro completable de arriba
                    System.out.println(testName + " - compose corriendo en el thread: " + Thread.currentThread().getName());
                    return CompletableFuture.supplyAsync(() ->{
                        System.out.println(testName + " - CompletableFuture interno corriendo en el thread: " + Thread.currentThread().getName());
                        return s + " World"  ;
                    } );
                });

        try {
            assertEquals("Hello World", completableFuture.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }

    class Person {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    @Test
    public void nt8(){

        String testName = "nt8";

        CompletableFuture<Person> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());

                    return "Pamela.18";
                })
                .thenCompose(s -> {
                    // s es el valor  que nos entrega el futuro completable de arriba
                    System.out.println(testName + " - compose corriendo en el thread: " + Thread.currentThread().getName());
                    List<String>  Persona =  Arrays.asList(s.split("\\."));
                    String nombre = Persona.get(0);
                    int edad = Integer.parseInt(Persona.get(1));

                    return CompletableFuture.supplyAsync(() ->{
                        System.out.println(testName + " - CompletableFuture interno corriendo en el thread: " + Thread.currentThread().getName());
                        return new Person(nombre,edad);
                    } );
                });

        try {
            assertEquals("Pamela", completableFuture.get().name);
            assertEquals(18, completableFuture.get().age);
        }catch(Exception e){
            assertTrue(false);
        }
    }



    @Test
    public void t9(){

        String testName = "t9";

        // El segundo parametro de thenCombina es un BiFunction la cual sí tiene que tener retorno.
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                    return "Hello";
                })
                .thenCombine(
                        CompletableFuture.supplyAsync(() ->
                        {
                            System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                        return " World";}),
                        (s1, s2) -> {
                            System.out.println(testName + " - future corriendo en el thread: " + Thread.currentThread().getName());
                           return s1 + s2;
                        });

        try {
            assertEquals("Hello World", completableFuture.get());
        }catch(Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void t10(){

        String testName = "t10";

        // El segundo parametro de thenAcceptBoth debe ser un BiConsumer. No puede tener retorno.
        CompletableFuture future = CompletableFuture.supplyAsync(() -> "Hello")
                .thenAcceptBoth(
                        CompletableFuture.supplyAsync(() -> " World"),
                        (s1, s2) -> System.out.println(testName + " corriendo en thread: "+Thread.currentThread().getName()+ " : " +s1 + s2));

        try{
            Object o = future.get();
        }catch(Exception e){
            assertTrue(false);

        }
    }

    @Test
    public void testEnlaceConSupplyAsinc(){
        String testName = "tprueba";

        ExecutorService es = Executors.newFixedThreadPool(1); // aun hilo
        CompletableFuture f = CompletableFuture.supplyAsync(()->"Hello",es);
        CompletableFuture<String> f2 = f.supplyAsync(()->{
            imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
            sleep(500);
            return "a";
        },es).supplyAsync(()->{
            imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
            return "b";
        },es);

        try{
            assertEquals(f2.get(),"b");
        }catch (Exception e){
            assertFalse(true);
        }
    }
    @Test
    public void t11(){

        String testName = "t11";

        ExecutorService es = Executors.newFixedThreadPool(1); // aun hilo
        CompletableFuture f = CompletableFuture.supplyAsync(()->"Hello",es); // "es" se le dice la caja de hilo que se va a usar

        f.supplyAsync(() -> "Hello")
                .thenCombineAsync(
                    CompletableFuture.supplyAsync(() -> {
                        System.out.println(testName + " thenCombineAsync en Thread (1): " + Thread.currentThread().getName());
                        return " World";
                    }),
                    (s1, s2) -> {
                        System.out.println(testName + " thenCombineAsync en Thread (2): " + Thread.currentThread().getName());
                        return s1 + s2;
                    },
                    es
                );

    }


    @Test
    public void testThenApplyAsync(){
        String testName = "tThenApplyAsync";

        ExecutorService es = Executors.newFixedThreadPool(3); // dos hilo explicitos

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() ->{
            imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
                    sleep(500);
                    return "a";
                },es);

        CompletableFuture<String> future = completableFuture.thenApplyAsync(s ->{
            imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
            sleep(500);
            return s + " b"; },es).thenApplyAsync(s ->{
            imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
            sleep(500);
            return s + " c"; },es).thenApplyAsync(s ->{
            imprimirMensaje(testName + " - future corriendo en el thread: "+Thread.currentThread().getName());
            sleep(500);
            return s + " d"; },es);
        try{
            assertEquals(future.get(),"a b c d" );
        }catch (Exception e){
            assertFalse(true);
        }
    }
}
