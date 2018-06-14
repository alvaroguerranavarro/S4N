package co.com.s4n.training.java.vavr;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.junit.Test;
import io.vavr.control.Option;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static io.vavr.collection.Iterator.empty;
import static org.junit.Assert.*;


/**
 *  Getting started de la documentacion de vavr http://www.vavr.io/vavr-docs/#_collections
 *  Javadoc de vavr collections https://static.javadoc.io/io.vavr/vavr/0.9.0/io/vavr/collection/package-frame.html
 */

public class ListSuite {

    /**
     * Lo que sucede cuando se intenta crear un lista de null
     */
    @Test(expected = NullPointerException.class)
    public void testListOfNull() {
        List<String> list1 = List.of(null);
        list1.get();
    }

    /**
     * Lo que sucede cuando se crea una lista vacía y se llama un método
     */
    @Test
    public void testZipOnEmptyList() {
        List<String> list = List.of();
        assertTrue("Failure - List should be empty",list.isEmpty());
        list.zip(empty());
    }

    @Test
    public void testHead(){
        List<Integer> list1 = List.of(1,2,3);
        Integer head = list1.head();
        assertEquals(head, new Integer(1));
    }

    @Test
    public void testTail(){
        List<Integer> list1 = List.of(1,2,3);
        List<Integer> expectedTail = List.of(2,3);
        List<Integer> tail = list1.tail();
        assertEquals(tail, expectedTail);
    }

    @Test
    public void testTailP(){
        List<Integer> list1 = List.of(1);
        List<Integer> expectedTail = List.of();
        List<Integer> tail = list1.tail();
        assertEquals(tail, expectedTail);
    }

    @Test(expected = java.util.NoSuchElementException.class)
    public void tesHeadP(){
        List<Integer> list1 = List.of();
        Integer expectedHead = 0;
        Integer head = list1.head();
        assertEquals(head, expectedHead);
    }

    @Test
    public void testHeadOption()
    {
        List<Integer> list1 = List.of();
        Option head = list1.headOption();
        assertEquals(head, Option.none());
    }


    @Test
    public void testHeadOption1()
    {
        List<Integer> list1 = List.of(1,2,3,4);
        Option head = list1.headOption();

        assertEquals(head,Option.some(1));
        assertEquals(1,head.getOrElse(1));
    }

    @Test
    public void testZip(){
        List<Integer> list1 = List.of(1,2,3);
        List<Integer> list2 = List.of(1,2,3);
        List<Tuple2<Integer, Integer>> zippedList = list1.zip(list2);
        assertEquals(zippedList.head(), Tuple.of(new Integer(1), new Integer(1)) );
        assertEquals(zippedList.tail().head(), Tuple.of(new Integer(2), new Integer(2)) );
    }

    /**
     * Una Lista es inmutable
     */
    @Test
    public void testListIsImmutable() {
        List<Integer> list1 = List.of(0, 1, 2);
        List<Integer> list2 = list1.map(i -> i);
        assertEquals(List.of(0, 1, 2),list1);
        assertNotSame(list1,list2);
    }

    public String nameOfNumer(int i){
        switch(i){
            case 1: return "uno";
            case 2: return "dos";
            case 3: return "tres";
            default: return "idk";
        }
    }

    @Test
    public void testMap(){

        List<Integer> list1 = List.of(1, 2, 3);
        List<String> list2 = list1.map(i -> nameOfNumer(i));

        assertEquals(list2, List.of("uno", "dos", "tres"));
        assertEquals(list1, List.of(1,2,3));

    }


    @Test
    public void testFilter(){
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> filteredList = list.filter(i -> i % 2 == 0);
        assertTrue(filteredList.get(0)==2);

    }


    /**
     * Se revisa el comportamiento cuando se pasa un iterador vacío
     */
    @Test
    public void testZipWhenEmpty() {
        List<String> list = List.of("I", "Mario's", "Please", "me");
        List<Tuple2<String, Integer>> zipped = list.zip(empty());
        assertTrue("Failure - The list should be empty",zipped.isEmpty());
    }

    /**
     * Se revisa el comportamiento cuando se pasa el iterador de otra lista
     */
    @Test
    public void testZipWhenNotEmpty() {
        List<String> list1 = List.of("I", "Mario's", "Please", "me", ":(");
        List<String> list2 = List.of("deleted", "test", "forgive", "!");
        List<Tuple2<String, String>> zipped2 = list1.zip(list2.iterator());
        List<Tuple2<String, String>> expected2 = List.of(Tuple.of("I", "deleted"), Tuple.of("Mario's", "test"),
                Tuple.of("Please", "forgive"), Tuple.of("me", "!"));
        assertEquals("Failure - The list wasn't match correctly.",expected2,zipped2);
    }

    /**
     * El zipWithIndex agrega numeración a cada item
     */
    @Test
    public void testZipWithIndex() {
        List<String> list = List.of("A", "B", "C");
        List<Tuple2<String, Integer>> expected = List.of(Tuple.of("A", 0), Tuple.of("B", 1), Tuple.of("C", 2));
        assertEquals("Failure - The list doesn't have the correct index.",expected,list.zipWithIndex());
    }

    /**
     *  pop y push por defecto trabajan para las pilas.
     */
    @Test
    public void testListStack() {
        List<String> list = List.of("B", "A");

        assertEquals("Failure pop does not drop the first element of the his list",
                List.of("A"), list.pop());

        assertEquals("Failure push does not add the element as the first in his list",
                List.of("D", "C", "B", "A"), list.push("C", "D"));

        assertEquals("Failure push does not add the element as the first in his list",
                List.of("C", "B", "A"), list.push("C"));

        assertEquals("Failure it's a lie first in last out",
                List.of("B", "A"), list.push("C").pop());

        assertEquals("Failure don't return the correct tuple",
                Tuple.of("B", List.of("A")), list.pop2());
    }

    /**
     * Una lista de vavr se comporta como una pila ya que guarda y
     * retorna sus elementos como LIFO.
     * Peek retorna el ultimo elemento en ingresar en la lista
     */
    @Test
    public void testLIFORetrieval() {
        List<String> list = List.empty();
        //Because vavr List is inmutable, we must capture the new list that the push method returns
        list = list.push("a");
        list = list.push("b");
        list = list.push("c");
        list = list.push("d");
        list = list.push("e");
        assertEquals("The list did not behave as a stack", List.of("d", "c", "b", "a"), list.pop());
        assertEquals("The list did not behave as a stack", "e", list.peek());
    }

    /**
     * Una lista puede ser filtrada dado un prediacado y el resultado
     * es guardado en una tupla
     */
    @Test
    public void testSpan() {
        List<String> list = List.of("a", "b", "c");
        Tuple2<List<String>, List<String>> tuple = list.span(s -> s.equals("a"));
        assertEquals("The first element of the tuple did not match", List.of("a"), tuple._1);
        assertEquals("The second element of the tuple did not match", List.of("b", "c"), tuple._2);
    }


    /**
     * Validar dos listas con la funcion Takewhile con los predicados el elemento menor a ocho y el elemento mayor a dos
     */
    @Test
    public void testListToTakeWhile() {
        List<Integer> myList = List.ofAll(4, 6, 8, 5);
        List<Integer> myListOne = List.ofAll(2, 4, 3);
        List<Integer> myListRes = myList.takeWhile(j -> j < 8);
        List<Integer> myListResOne = myListOne.takeWhile(j -> j > 2);
        assertTrue("List with values less than eight", myListRes.nonEmpty());
        assertEquals("List with length of two", 2, myListRes.length());
        assertEquals("List with last value six", new Integer(6), myListRes.last());
        assertTrue("List with values greater than two", myListResOne.isEmpty());
    }

    /**
     * Se puede separar una lista en ventanas de un tamaño especifico
     */
    @Test
    public void testSliding(){
        List<String> list = List.of(
                "First",
                "window",
                "!",
                "???",
                "???",
                "???");
        assertEquals("Failure - the window is incorrect",List.of("First","window","!"),list.sliding(3).head());
    }

    /**
     * Al dividir una lista en ventanas se puede especificar el tamaño del salto antes de crear la siguiente ventana
     */
    @Test
    public void testSlidingWithExplicitStep(){
        List<String> list = List.of(
                "First",
                "window",
                "!",
                "Second",
                "window",
                "!");
        List<List<String>> windows = list.sliding(3,3).toList(); // Iterator -> List
        assertEquals("Failure - the window is incorrect",
                List.of("Second","window","!"),
                windows.get(1));
        List<List<String>> windows2 = list.sliding(3,1).toList(); // Iterator -> List
        assertEquals("Failure - the window is incorrect",
                List.of("window","!","Second"),
                windows2.get(1));
    }
}