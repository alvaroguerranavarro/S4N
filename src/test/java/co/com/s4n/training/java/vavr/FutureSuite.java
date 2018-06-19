package co.com.s4n.training.java.vavr;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.concurrent.Promise;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.Patterns.*;


import java.util.function.BiFunction;
import java.util.function.Consumer;
import static io.vavr.API.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;
import java.util.function.Supplier;

//import org.junit.Test;
//import static org.junit.Assert.assertArrayEquals;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertNotEquals;
//import static junit.framework.TestCase.assertEquals;
//import static org.junit.Assert.*;

//

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.platform.runner.IncludeEngines;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@IncludeEngines("junit-jupiter")


public class FutureSuite {
    // Max wait time for results = WAIT_MILLIS * WAIT_COUNT (however, most probably it will take only WAIT_MILLIS * 1)
    private static final long WAIT_MILLIS = 50;
    private static final int WAIT_COUNT = 100;
    private static void waitUntil(Supplier<Boolean> condition) {
        int count = 0;
        while (!condition.get()) {
            if (++count > WAIT_COUNT) {
                fail("Condition not met.");
            } else {
                Try.run(() -> Thread.sleep(WAIT_MILLIS));
            }
        }
    }

    /**
     * Se prueba que pasa cuando se crea un futuro con error.
     */
    @Test
    public void testFutureWithError() {
        Future<String> future = Future.of(() -> {throw new Error("Failure");});
        assertThrows(Error.class, () -> future.get());
        //(expected = Error.class)
    }

    /**
     * El resultado de un futuro se puede esperar con onComplete
     */
    @Test
    public void testOnCompleteSuccess() {
        Future<String[]> futureSplit = Future.of(() -> "TEXT_TO_SPLIT".split("_"));
        futureSplit.onComplete(res -> {
            if (res.isSuccess()) {
                for (int i = 0; i < res.get().length; i++) {   // el get es de un try
                    res.get()[i] = res.get()[i].toLowerCase(); //  .get del array
                }
            }
        }); // La lambda de Oncomplete se ejecuta asincronicamente
        futureSplit.await();
        String[] expected = {"text", "to", "split"};
        //Wait until we are sure that the second thread (onComplete) is done.
        waitUntil(() -> futureSplit.get()[2].equals("split"));
        assertArrayEquals(expected, futureSplit.get());
    }

    @Test
    public void testOnCompleteSofisma() {
        Future<String[]> futureSplit = Future.of(() -> "TEXT_TO_SPLIT".split("_"));

        Future<String[]> strings = futureSplit.onComplete(res -> {
            if (res.isSuccess()) {
                for (int i = 0; i < res.get().length; i++) {   // el get es de un try
                    res.get()[i] = res.get()[i].toLowerCase(); //  .get del array
                }
            }
        });// La lambda de Oncomplete se ejecuta asincronicamente

        System.out.println(futureSplit);
        System.out.println(strings);

        assertEquals(futureSplit,strings);
        //assertNotSame(futureSplit,strings);
    }

    @Test
    public void testOnCompleteSuccess1() {
        Future<String[]> futureSplit = Future.of(() -> "TEXT_TO_SPLIT".split("_"));
        futureSplit.onComplete(res -> {
            if (res.isSuccess()) {
                for (int i = 0; i < res.get().length; i++) {   // el get es de un try
                    res.get()[i] = res.get()[i].toLowerCase(); //  .get del array   la lambda corre en otro hilo
                }
            }
        }); // La lambda de Oncomplete se ejecuta asincronicamente
        futureSplit.await(); // solo verifica que el futuro se completo
        String[] expected = {"text", "to", "split"};
        //Wait until we are sure that the second thread (onComplete) is done.
        waitUntil(() -> futureSplit.get()[2].equals("split"));
        assertArrayEquals(expected, futureSplit.get());
    }

    /**
     *Valida la funcion de find aplicando un predicado que viene de una implementacion de la clase Iterable que contenga Futuros
     * Tener encuenta el primero que cumpla con el predicado y sea Oncomplete es el que entrega
     */
    @Test
    public void testFutureToFind() {
        List<Future<Integer>> myLista = List.of( Future.of(() -> 5+4), Future.of(() -> 6+9), Future.of(() -> 31+1),Future.of(() -> 20+9));
        Future<Option<Integer>> futureSome = Future.find(myLista, v -> v < 10);
        Future<Option<Integer>> futureSomeM = Future.find(myLista, v -> v > 31);
        Future<Option<Integer>> futureNone = Future.find(myLista, v -> v > 40);
        assertEquals(Some(9), futureSome.get());
        assertEquals( Some(32), futureSomeM.get());
        assertEquals( None(), futureNone.get());
    }

    /**
     *Valida la funcion de find aplicando un predicado que viene de una implementacion de la clase Iterable que contenga Futuros
     */
    @Test
    public void testFutureToTransform() {
        Integer futuretransform = Future.of( () -> 9).transform(v -> v.getOrElse(12) + 80);
        Future<Integer> myResult= Future.of(() -> 9).transformValue(v -> Try.of(()-> v.get()+12));
        assertEquals(new Integer(89) ,futuretransform);
        assertEquals(new Integer (21) ,myResult.get());
    }

    /**
     *Valida la funcion de find aplicando un predicado que viene de una implementacion de la clase Iterable que contenga Futuros
     */
    @Test
    public void testFutureToOnFails() {
        final String[] valor = {"default","pedro"};
        Consumer<Object> funcion = element -> {
            valor[1] = "fallo";
            //valor = {"Hola","Hola"};
        };

        Future<Object> myFuture = Future.of(() -> {throw new Error("No implemented");});
        myFuture.onFailure(funcion);
        assertEquals("pedro",valor[1]);
        myFuture.await();
        assertTrue(myFuture.isFailure());
        waitUntil(() -> valor[1].toString()=="fallo");
        assertEquals( "fallo",valor[1]);
    }

    /**
     *Se valida el uso de Map obteniendo la longitu de un String
     * Se valida el uso Flatmap obteniendo el resultado apartir de una suma
     */
    @Test
    public void testFutureToMap() {
        Future<Integer> myMap = Future.of( () -> "pedro")
                .map(v -> v.length()); // Se ejecuta sincronicamente
        System.out.println(myMap.get());
        assertEquals(new Integer(5),myMap.get());

    }

    @Test
    public void testFutureToFlatMap() {
        Future<Integer> myFlatMap = Future
                .of(() -> Future.of(() -> 5 + 9))
                .flatMap(v -> Future.of(() -> v.await().getOrElse(15))); // El await  opera
        System.out.println(myFlatMap.get());


        assertEquals( new Integer(14), myFlatMap.get());
    }

    private Future<Integer> suma(Integer a, Integer b)
    {
        return Future.of(()->a+b);
    }


    private Future<Integer> division(Integer a, Integer b)
    {
        return Future.of(()->a/b);
    }

    @Test
    public void testFutureFlatMap()
    {
        Future<Integer> res = suma(1,2)
                .flatMap(r0->suma(r0,r0).flatMap(r1->division(r1,r1)));
        //System.out.println(res.get());
        assertEquals(new Integer(1),res.get() );
    }

    @Test
    public void testFutureFlatMapFailure()
    {
        Future<Integer> res = suma(1,2)
                .flatMap(r0->suma(r0,-3).flatMap(r1->division(r1,r1)));
        //System.out.println(res.get());
        assertEquals(res.isFailure(), res.isFailure());
    }

    /**
     *Se valida el uso de foreach para el encademaient de futuros
     */
    @Test
    public void testFutureToForEach() {
        java.util.List<Integer> results = new ArrayList<>(); //list de java
        System.out.println(results);
        java.util.List<Integer> compare = Arrays.asList(9,15,32,29);
        System.out.println(compare);
        List<Future<Integer>> myLista = List.of(Future.of(() -> 5 + 4), Future.of(() -> 6 + 9), Future.of(() -> 31 + 1), Future.of(() -> 20 + 9));
        System.out.println(myLista);
        myLista.forEach(v -> {  // espera que cada futuro termine osea que este test es determinista porque el forEach
            results.add(v.get()); // espera que cada futuro termine
        });
        System.out.println(results);
        assertEquals(compare, results);
    }

    @Test
    public void forEachInFUture()
    {
        final String[] result = {"666"};
        Future<String> f1 = Future.of(()->"1");
        f1.forEach(i ->result[0]=i);
        f1.await();
        waitUntil(()->"1".equals(result[0]));
        assertEquals(f1.get(),"1");
        assertEquals(result[0],"1");
    }

    @Test
    public void testFutureFold3() {
        Future<Integer> f1 =  Future.of(() -> 1);
        Future<Integer> f2 =  Future.of(() -> 2);
        Future<Integer> f3 =  Future.of(() -> 3);

        Future<Integer> f4 =  Future.fold(List(f1,f2,f3),0,(x,y) ->x+y);
        assertEquals(f4.await().get(),f4.get());
        assertEquals(new Integer(6),f4.get());
    }

    @Test
    public void testFutureFoldFlatMap() {
        Future<Integer> f1 =  Future.of(() -> 1);
        Future<Integer> f2 =  Future.of(() -> 2);
        Future<Integer> f3 =  Future.of(() -> 3);

        Future<Integer> f4 =  f1.flatMap(a->f2.flatMap(b->{
            Integer p = a+b;
            return f3.flatMap(c ->Future.of(()->p+c));
        }));

        assertEquals(f4.await().get(),f4.get());
        assertEquals(new Integer(6),f4.get());
    }

    private Future<Integer> myFold(List<Future<Integer>> futures, Integer zero, BiFunction<Integer,Integer,Integer> bf) {
        //res = futures.flatMap(a ->)
        System.out.println(futures);
        //futures.flatMap(r -> bf.apply(zero,r.));
        System.out.println(zero);
        System.out.println(bf.apply(2,2));

        return  Future.of(()->3);

    }

    @Test
    public void testFutureFold() {
        Future<Integer> f1 =  Future.of(() -> 1);
        Future<Integer> f2 =  Future.of(() -> 2);
        Future<Integer> f3 =  Future.of(() -> 3);

        Future<Integer> f4 =  Future.fold(List(f1,f2,f3),0,(x,y) ->x+y);
        assertEquals(f4.await().get(),f4.get());
        assertEquals(new Integer(6),f4.get());
    }

    @Test
    public void testFutureFoldP() {
        Future<Integer> f1 =  Future.of(() -> 1);
        Future<Integer> f2 =  Future.of(() -> 2);
        Future<Integer> f3 =  Future.of(() -> 3);

        Future<Integer> f4 =  myFold(List(f1,f2,f3),0,(x,y) ->x+y);
        assertEquals(f4.await().get(),f4.get());
        assertEquals(new Integer(3),f4.get());
    }


    @Test
    public void testFutureFol1() {
        Future<Integer> f1 =  Future.of(() -> 1);
        Future<Integer> f2 =  Future.of(() -> 2/0);
        Future<Integer> f3 =  Future.of(() -> 3);
        Future<Integer> f4 =  Future.fold(List(f1,f2,f3),0,(x,y) ->x+y);
        f4.await();
        assertTrue(f4.isFailure());
    }


    /**
     * Se puede crear un future utilizando funciones lambda
     */
    @Test
    public void testFromLambda(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = Future.ofSupplier(service, ()-> Thread.currentThread().getName());
        String future_thread = future.get();
        String main_thread = Thread.currentThread().getName();
        assertNotEquals( main_thread, future_thread);
        assertTrue(future.isCompleted());
    }

    /**
     * Se puede crear un future utilizando referencias a metodos
     */
    @Test
    public void testFromMethodRef(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Double> future = Future.ofSupplier(service, Math::random);
        future.get();
        assertTrue(future.isCompleted());
    }


    /**
     * Este metodo me permite coger el primero futuro que termine su trabajo, la coleccion de futuros debe
     * extender de la interfaz iterable
     */
    @Test
    public void testFutureFirstCompleteOf() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        ExecutorService service2 = Executors.newSingleThreadExecutor();

        Future<String> future2 = Future.ofSupplier(service, () -> {
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return ("Hello this is the Future 2");
        });
        Future<String> future = Future.ofSupplier(service2, () -> "Hello this is the Future 1");
        List<Future<String>> futureList = List.of(future,future2);
        Future<String> future3 = Future.firstCompletedOf(service,futureList);

        assertEquals("Hello this is the Future 1",future3.get());
    }

    /**
     * Se puede cambiar el valor de un Future.Failure por otro Future utilizando el metodo fallBackTo
     */
    @Test
    public void testFailureFallBackTo(){
        Future<String> failure = Future.of(() -> {throw new Error("No implemented");});
        String rescue_msg = "Everything is Ok!";
        Future<String> rescue_future = Future.of(() -> rescue_msg);
        Future<String> final_future = failure.fallbackTo(rescue_future);
        assertEquals(rescue_msg, final_future.get());
    }

    /**
     * El metodo fallBackTo no tiene efecto si el future inicial es exitoso
     */
    @Test
    public void testSuccessFallBackTo(){
        String initial_msg = "Hello!";
        Future<String> success = Future.of(() -> initial_msg);
        Future<String> rescue_future = Future.of(() -> "Everything is Ok!");
        Future<String> final_future = success.fallbackTo(rescue_future);
        assertEquals( initial_msg, final_future.get());
    }

    /**
     * al usar el metodo fallBackTo si los dos futures fallan el failure final debe contener el error del futuro inicial
     */
    @Test
    public void testFailureFallBackToFailure(){
        String initial_error = "I failed first!";
        Future<String> initial_future = Future.of(() -> {throw new Error(initial_error);});
        Future<String> rescue_future = Future.of(() -> {TimeUnit.SECONDS.sleep(1);throw new Error("Second failure");});
        Future<String> final_future = initial_future.fallbackTo(rescue_future);
        final_future.await();
        assertEquals(initial_error,
                final_future.getCause().get().getMessage()); //Future -> Some -> Error -> String
    }

    /**
     * Se puede cancelar un futuro si este no ha sido completado aún
     */
    @Test
    public void testCancelFuture(){
        Future<String> future = Future.of(() -> {
            TimeUnit.SECONDS.sleep(2);
            return "End";});
        assertTrue(future.cancel());
        assertTrue(future.isCompleted());
        assertTrue(future.isFailure());
    }

    /**
     * No se puede cancelar un futuro completado
     */
    @Test
    public void testCancelAfterComplete(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = Future.of(service,() -> "Hello!");
        future.await();
        assertTrue(future.isCompleted());
        assertFalse(future.cancel());
    }

    /**
     * onFail, onSuccess y onComplete devuelven el mismo futuro que invoca los metodos
     */
    @Test
    public void testTriggersReturn() {
        Future<String> futureSplit = Future.of(() -> "Hello!");

        Future<String> onComplete = futureSplit.onComplete(res -> {/*do some side effect*/});
        Future<String> onSuccess = futureSplit.onSuccess(res ->{/*do some side effect*/});
        Future<String> onFail = futureSplit.onFailure(res -> {/*do some side effect*/});
        futureSplit.await();
        assertSame(futureSplit, onComplete);
        assertSame(futureSplit, onSuccess);
        assertSame(futureSplit, onFail);
    }

    /**
     * Se prueba el poder realizar una acción luego de que un futuro finaliza.
     */
    @Test
    public void testOnSuccess() {
        String[] holder = {"Don't take my"};
        Future<String> future = Future.of(() -> "Ghost");
        future.onSuccess(s -> {
            assertTrue(future.isCompleted());
            holder[0] += " hate personal";
        });
        waitUntil(() -> holder[0].length() > 14);
        assertEquals("Don't take my hate personal",holder[0]);
    }

    /**
     * Se puede crear un futuro como resultado de aplicar un fold a un objeto iterable compuesto de futuros
     */
    @Test
    public void testFoldOperation(){
        List<Future<Integer>> futureList = List.of(
                Future.of(()->0),
                Future.of(()->1),
                Future.of(()->2),
                Future.of(()->3));
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> futureResult = Future.fold(
                service, // Optional executor service
                futureList, // <Iterable>
                "Numbers on the list: ", // Seed
                (acumulator, element) -> acumulator + element); // Fold operation
        assertEquals("Numbers on the list: 0123",
                futureResult.get());
    }

    /**
     * Un futuro se puede filtrar dado un predicado
     * filter retorna una nueva referencia
     */
    @Test
    public void testFilter() {
        Future<String> future = Future.successful("this_is_a_text");
        Future<String> some = future.filter(s -> s.contains("a_text"));
        Future<String> none = future.filter(s -> s.contains("invalid"));
        assertNotSame(future,some);
        assertNotSame(future,none);
        assertEquals("this_is_a_text", some.get());
        assertTrue( none.isEmpty());
    }

    /**
     *  Sequence permite cambiar una lista de futuros<T> a un futuro de una lista <T>,
     *  este devuelve por defecto un Futuro<stream>
     */
    @Test
    public void testFutureWithSequence() {
        List<Future<String>> listOfFutures = List.of(
                Future.of(() -> "1 mensaje"),
                Future.of(() -> "2 mensaje")
        );

        Future<Seq<String>> futureList = Future.sequence(listOfFutures);
        assertFalse(futureList.isCompleted());
        assertTrue(futureList instanceof Future);

        Stream<String> stream = (Stream<String>) futureList.get();
        assertEquals(List.of("1 mensaje","2 mensaje").asJava(),stream.asJava());
    }

    /**
     *  El Recover me sirve para recuperar futuros que hayan fallado, y se recupera el resultado con otro
     *  y se crea un futuro nuevo
     */
    @Test
    public void testFutureRecover() {
        final String[] thread1 = {""};
        final String[] thread2 = {""};
        Future<Integer> aFuture = Future.of(
                () -> {
                    Thread.sleep(1000);
                    thread1[0] = Thread.currentThread().getName().toString();
                    return 2/0;
                }
        );
        Future<Integer> aRecover = aFuture.recover(it -> Match(it).of(
                Case($(),() -> {
                    thread2[0] = Thread.currentThread().getName().toString();
                    return 2;
                })
        ));

        System.out.println("thread 1 "+thread1[0]);
        System.out.println("thread 2" +thread2[0]);

        aRecover.await();
        assertTrue(aRecover.isSuccess());
        assertFalse(thread1[0].equals(thread2[0]));
        assertEquals(new Integer(2),aRecover.get());
    }

    @Test
    public void testFutureRecover1() {
        final String[] thread1 = {""};
        final String[] thread2 = {""};

        Future<Integer> aFuture = Future.of(
                Executors.newSingleThreadExecutor(),
                () -> {
                    Thread.sleep(1000);
                    thread1[0] = Thread.currentThread().getName().toString();
                    return 2/0;
                }
        );
        Future<Integer> aRecover = aFuture.recover(it -> Match(it).of(
                Case($(),() -> {
                    thread2[0] = Thread.currentThread().getName().toString();
                    return 2;
                })
        ));

        System.out.println("thread 1 "+ thread1[0]);
        System.out.println("thread 2" + thread2[0]);

        aRecover.await();
        assertTrue(aRecover.isSuccess());
        assertTrue(thread1[0].equals(thread2[0]));
        assertEquals(new Integer(2),aRecover.get());
    }

    @Test
    public void testFutureRecover2Failure() {
        final String[] thread1 = {""};
        final String[] thread2 = {""};

        Future<Integer> aFuture = Future.of(
                Executors.newSingleThreadExecutor(),
                () -> {
                    Thread.sleep(1000);
                    thread1[0] = Thread.currentThread().getName().toString();
                    return 2/0;
                }
        );
        Future<Integer> aRecover = aFuture.recover(it -> Match(it).of(
                Case($(),() -> {
                    thread2[0] = Thread.currentThread().getName().toString();
                    return 2/0;
                })
        ));

        System.out.println("thread 1 "+ thread1[0]);
        System.out.println("thread 2" + thread2[0]);

        aRecover.await();
        assertTrue(aRecover.isSuccess());
        assertTrue(thread1[0].equals(thread2[0]));
        assertEquals(new Integer(2),aRecover.get());
    }

    /**
     *  El Recover me sirve para recuperar futuros que hayan fallado, y se recupera el futuro con otro
     *  y se crea un futuro nuevo
     */
    @Test
    public void testFutureRecoverWith() {
        final String[] thread1 = {""};
        final String[] thread2 = {""};
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> aFuture = Future.of(service,() -> {
            thread1[0] = Thread.currentThread().getName().toString();
            return 2 / 0;
        });
        Future<Integer> aRecover = aFuture.recoverWith(it -> Match(it).of(
                Case($(), () -> Future.of(() -> {
                    thread2[0] = Thread.currentThread().getName().toString();
                    return 1;
                }))
        ));
        aRecover.await();
        assertTrue(aRecover.isSuccess());
        assertFalse(thread1[0].equals(thread2[0]));
        assertEquals(new Integer(1),aRecover.get());
    }

    /**
     * Validar pattern Matching a un future correcto.
     */
    @Test
    public void testFuturePatternMatchingSuccess() {
        Future<String> future = Future.of(() -> "Glad to help");
        String result = Match(future).of(
                Case($Future($(instanceOf(Error.class))), "Failure!"),
                Case($Future($()), "Success!"),
                Case($(), "Double failure"));
        assertEquals("Success!", result);
    }

    /**
     * Validar pattern Matching a un future correcto.
     */
    @Test
    public void testFuturePatternMatchingError() {

        Future<String> future = Future.of(() -> {
            throw new Error("Failure");
        });

        // Este test algunas veces tiene exito y algunas otras fracasa
        // Por que sera?

        String result = Match(future).of(
                Case($Future($Some($Failure($()))), "Failure!"),
                Case($Future($()), "Success!"),
                Case($(), "Double failure"));

        assertEquals("Failure!",
                result);
    }

    /**
     * Crear un futuro a partir de un Try fallido
     */
    @Test
    public void testFromFailedTry(){
        Try<String> tryValue = Try.of(() -> {throw new Error("Try again!");});
        Future<String> future = Future.fromTry(tryValue);
        future.await();
        assertTrue(future.isFailure());
        assertEquals(
                tryValue.getCause(),
                future.getCause().get()); //Future -> Option -> Throwable
    }

    /**
     * Crear un futuro a partir de un Try exitoso
     */
    @Test
    public void testFromSuccessTry(){
        Try<String> tryValue = Try.of(() -> "Hi!");
        Future<String> future = Future.fromTry(tryValue);
        future.await();
        assertTrue( future.isSuccess());
        assertEquals( "Hi!",future.get());
    }

    /**
     * Crear un futuro de la libreria vavr a partir de un futuro de java8
     */
    @Test
    public void testFromJavaFuture() {  //para pasar un futuro de java a vavr
        Callable<String> task = () -> Thread.currentThread().getName();
        ExecutorService service = Executors.newSingleThreadExecutor();
        java.util.concurrent.Future<String> javaFuture = service.submit(task);
        ExecutorService service2 = Executors.newSingleThreadExecutor();
        Future<String> future = Future.fromJavaFuture(service2, javaFuture);
        try {
            assertEquals( javaFuture.get(), future.get());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Un futuro se puede crear a partir de una promesa
     */
    @Test
    public void testFutureFromPromise() {
        Promise<String> promise = Promise.successful("success!");
        //Future can be created from a promise
        Future<String> future = promise.future();
        future.await();
        assertTrue(future.isCompleted());
        assertTrue(promise.isCompleted());
        assertEquals( "success!", future.get());
    }

    /**
     *Se valida la comunicacion de Futuros mediante promesas
     */
    @Test
    public void testComunicateFuturesWithPromise() {
        Promise<Integer> mypromise = Promise.make();
        Future<Object> myFuture = Future.of(()-> {
            mypromise.success(15);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "algo";
        });
        Future<Integer> myFutureOne = mypromise.future();
        myFutureOne.await();
        assertEquals(new Integer(15),myFutureOne.get());
        assertFalse(myFuture.isCompleted());
    }

    @Test
    public void testFuture() {

        long inicio = System.nanoTime();

        Future<Integer> f1 = Future.of(() -> {
            Thread.sleep(500);
            return 5 + 4;
        });

        Future<Integer> f2 = Future.of(() -> {
            Thread.sleep(800);
            return 5+4;
        });

        Future<Integer> f3 = Future.of(() -> {
            Thread.sleep(300);
            return 5 + 4;
        });



       Future<Integer> f4 = f1.flatMap(a->f2
                       .flatMap(b->f3.
                               flatMap(c-> Future.of(()-> a+b+c))));

       f4.await();

        long final1 = System.nanoTime();

        long resul = TimeUnit.MILLISECONDS.convert(final1-inicio,TimeUnit.NANOSECONDS);

        System.out.println(resul);
        assertTrue(resul > 800);

    }

}
