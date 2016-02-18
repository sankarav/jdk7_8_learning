package edu.ub.san.streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by san on 2/16/16 for jdk7_8_learning.
 */
public class BeforeStreams {

    static List<String> names = Arrays.asList("alpha","bravo","charlie","delta",
            "echo","foxtrot","golf","hotel","india","juliet");


    public static void forEachDemo(){
        //forEach element in the list names SOUT the value
        //s is the temp variable to which each value is assigned
        names.forEach(s -> System.out.println(s));
    }

    public static void replaceAllDemo(){
        //Replace Every element with the given lambda
        names.replaceAll(s -> s.toUpperCase());
        System.out.println(names);
    }

    public static void removeIfDemo(){
        List<String> temp = new ArrayList<>(names);

        //if the passed in predicate is true, the item is removed
        temp.removeIf(s -> s.length() < 6);
        System.out.println(temp);
    }

    public static void main(String[] args) {
        //forEachDemo();
        //replaceAllDemo();
        removeIfDemo();
    }

}
