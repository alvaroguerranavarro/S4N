package co.com.s4n.training.java.vavr;

import co.com.s4n.training.java.jdk.Prueba;
import org.junit.Test;


import io.vavr.PartialFunction;
import io.vavr.control.Option;

import static io.vavr.API.None;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.util.ArrayList;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;

import java.util.List;
import java.util.Optional;

import static io.vavr.API.Some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OptionSuite {


    /**
     * Un option se puede filtar, y mostrar un some() o un none si no encuentra el resultado
     */
    @Test
    public void testOptionWithFilter() {
        Option<Integer> o = Option(3);

        assertEquals("Does not Exist the filter",
                Some(3),
                o.filter(it -> it >= 3));

        assertEquals("Does not Exist the filter",
                None(),
                o.filter(it -> it > 3));
    }

    /**
     * Se puede hacer pattern matching a un option y comparar entre Some y None.
     */
    private String patternMatchSimple(Option<Integer> number) {
        String result = Match(number).of(
                Case($Some($()),"Existe"),
                Case($None(),"Imaginario")
        );
        return result;
    }

    @Test
    public void testOptionWithPatternMatching() {
        Option<Integer> o1 = Option(1);
        Option<Integer> o2 = None();

        //Comparacion de Some o None()
        assertEquals("Failure match optionList", "Existe", patternMatchSimple(o1));
        assertEquals("Failure match optionList2", "Imaginario", patternMatchSimple(o2));
    }
    /**
     *
     * el metodo peek aplica una funcion lambda o un metodo con el valor de Option cuando esta definido
     * este metodo se usa para efectos colaterales y retorna el mismo Option que lo llamó
     */
    @Test
    public void testPeekMethod(){
        Option<String> defined_option = Option.of("Hello!");
        /* Se debe utilizar una variable mutable para reflejar los efectos colaterales*/
        final List<String> list = new ArrayList<>();
        Option<String> peek = defined_option.peek(list::add);// the same as defined_option.peek(s -> list.add(s))

        System.out.println("peek: "+ peek);

        assertEquals("failed - peek did not return the same Option value",
                Option.of("Hello!"),
                defined_option);

        assertEquals("failed - peek did not apply the side effect",
                "Hello!",
                list.get(0));
    }

    /**
     * Un option se puede transformar dada una función
     */
    @Test
    public void testOptionTransform() {
        String textToCount = "Count this text";
        Option<String> text = Option.of(textToCount);
        Option<Integer> count = text.transform(s -> Option.of(s.getOrElse("DEFAULT").length()));

        assertEquals("failure - Option was not transformed",
                Option.of(textToCount.length()),
                count);

        Option<String> hello = Option.of("Hello");
        Tuple2<String, String> result = hello.transform(s -> Tuple.of("OK", s.getOrElse("DEFAULT")));

        assertEquals("failure - Option was not transformed",
                Tuple.of("OK", "Hello"),
                result);

    }

    /**
     * el metodo getOrElse permite obtener el valor de un Option o un sustituto en caso de ser None
     */
    @Test
    public void testGetOrElse(){
        Option<String> defined_option = Option.of("Hello!");
        Option<String> none = None();
        assertEquals("failure - getOrElse did not get the current value of Option", "Hello!", defined_option.getOrElse("Goodbye!"));
        assertEquals("failure - getOrElse did not replace None", "Goodbye!", none.getOrElse("Goodbye!"));
    }

    /**
     * el metodo 'when' permite crear un Some(valor) o None utilizando condicionales booleanos
     */
    @Test
    public void testWhenMethod(){
        Option<String> valid = Option.when(true, "Good!");
        Option<String> invalid = Option.when(false, "Bad!");
        assertEquals("failed - the Option value must contain a Some('Good!')", Some("Good!"), valid);
        assertEquals("failed - the Option value must contein a None because the condtion is false", None(), invalid);
    }

    @Test
    public void testOptionCollect() {
        final PartialFunction<Integer, String> pf = new PartialFunction<Integer, String>() {
            @Override
            public String apply(Integer i) {
                return String.valueOf(i);
            }

            @Override
            public boolean isDefinedAt(Integer i) {
                return i % 2 == 1;
            }
        };
        assertEquals("Failure, it returned Some() it should returned None()", None(),Option.of(2).collect(pf));
        assertEquals("Failure, it returned Some() it should returned None()", None(),Option.<Integer>none().collect(pf));
    }
    /**
     * En este test se prueba la funcionalidad para el manejo de Null en Option con FlatMap
     */
    @Test
    public void testMananagementNull(){
        Option<String> valor = Option.of("pepe");
        Option<String> someN = valor.map(v -> null);

        /* Se valida que devuelve un Some null lo cual podria ocasionar en una Excepcion de JavanullPointerExcepcion*/
        assertEquals("The option someN is Some(null)",
                someN.get(),
                null);

        Option<String> buenUso = someN
                .flatMap(v -> {
                    System.out.println("testManagementNull - Esto se imprime? (flatMap)");
                    return Option.of(v);
                })
                .map(x -> {
                    System.out.println("testManagementNull - Esto se imprime? (map)");
                    return x.toUpperCase() +"Validacion";
                });

        assertEquals("The option is not defined because result is None",
                None(),
                buenUso);
    }

    /**
     * En este test se prueba la funcionalidad para transformar un Option por medio de Map y flatMap
     */
    @Test
    public void testMapAndFlatMapToOption() {
        Option<String> myMap = Option.of("mi mapa");

        Option<String> myResultMapOne = myMap.map(s -> s + " es bonito");

        assertEquals("Transform Option with Map",
                Option.of("mi mapa es bonito"),
                myResultMapOne);

        Option<String> myResultMapTwo = myMap
                .flatMap(s -> Option.of(s + " es bonito"))
                .map(v -> v + " con flat map");


        assertEquals("Transform Option with flatMap",
                Option.of("mi mapa es bonito con flat map"),
                myResultMapTwo);
    }

    @Test
    public void optionFromNull(){
        Option<Object> of = Option.of(null);
        assertEquals(of, None());
    }

    @Test
    public void optionFromOptional(){
        Optional optional = Optional.of(1);
        Option option = Option.ofOptional(optional);
    }

    Option<Integer> esPar(int i){
        return (i%2==0)?Some(i):None();
    }

    @Test
    public void forCompEnOption1(){
        Option<Integer> integers = For(esPar(2), d -> Option(d)).toOption();
        assertEquals(integers,Some(2));
    }

    @Test
    public void forCompEnOption2(){
        Option<Integer> integers = For(esPar(2), d ->
                                   For(esPar(4), c -> Option(d+c))).toOption();
        assertEquals(integers,Some(6));
    }

    @Test
    public void flatMapInOPtionMultiplicar(){
        Option<Integer> multiplicar = Prueba.multiplicar(4,3).flatMap(a -> Prueba.multiplicar(a,1))
                .flatMap(b -> Prueba.division(b,1))
                .flatMap(c->Prueba.comparar(c,1))
                .flatMap(d ->Prueba.postivo(d));
        assertEquals(multiplicar.getOrElse(666).intValue(),12);
    }
    @Test
    public void flatMapInOPtionForMultiplicar(){
        Option<Integer> res =
                For(Prueba.multiplicar(3,1), r1 ->
                        For(Prueba.division(r1,1), r2 ->
                                For(Prueba.comparar(r2,1), r3 -> Prueba.postivo(r3)))).toOption();
        assertEquals(res.getOrElse(666).intValue(),3);
    }
    @Test
    public void flatMapInOPtionComparar(){
        Option<Integer> multiplicar = Prueba.comparar(2,1).flatMap(a -> Prueba.comparar(a,1))
                .flatMap(b -> Prueba.comparar(b,1))
                .flatMap(c->Prueba.comparar(c,1))
                .flatMap(d ->Prueba.comparar(d,1));
        assertEquals(multiplicar.getOrElse(666).intValue(),2);
    }
    @Test
    public void flatMapInOPtionForComparar(){
        Option<Integer> Comparar =
                For(Prueba.comparar(5,5), r1 ->
                        For(Prueba.comparar(r1,5), r2 ->
                                For(Prueba.comparar(r2,5), r3 -> Prueba.comparar(r3,5)))).toOption();
        assertEquals(Comparar.getOrElse(666).intValue(),5);
    }
    @Test
    public void flatMapInOPtionDivision(){
        Option<Integer> division = Prueba.division(50,2).flatMap(a -> Prueba.division(a,1))
                .flatMap(b -> Prueba.division(b,1))
                .flatMap(c->Prueba.division(c,1))
                .flatMap(d ->Prueba.division(d,1));
        assertEquals(division.getOrElse(666).intValue(),25);
    }
    @Test
    public void flatMapInOPtionForDivision(){
        Option<Integer> division =
                For(Prueba.division(15,3), r1 ->
                        For(Prueba.division(r1,1), r2 ->
                                For(Prueba.division(r2,1), r3 -> Prueba.division(r3,1)))).toOption();
        assertEquals(division.getOrElse(666).intValue(),5);
    }

    @Test
    public void flatMapInOPtionPositivo(){
        Option<Integer> positivo = Prueba.postivo(-50).flatMap(a -> Prueba.postivo(a))
                .flatMap(b -> Prueba.postivo(b))
                .flatMap(c->Prueba.postivo(c-50))
                .flatMap(d ->Prueba.postivo(d));
        assertEquals(positivo.getOrElse(666).intValue(),-100);
    }

    @Test
    public void flatMapInOPtionForPositivo(){
        Option<Integer> positivo =
                For(Prueba.postivo(15), r1 ->
                        For(Prueba.postivo(-r1), r2 ->
                                For(Prueba.postivo(r2+15), r3 -> Prueba.postivo(r3)))).toOption();
        assertEquals(positivo.getOrElse(666).intValue(),0);
    }
}
