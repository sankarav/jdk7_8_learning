package edu.ub.san.streams;

import edu.ub.san.Util;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Created by san on 2/16/16 for jdk7_8_learning.
 *
 * Code samples of using J2SE Streams
 *
 */
public class StreamsFirst {

    static List<String> names = Arrays.asList("alpha","bravo","charlie","delta",
            "echo","foxtrot","golf","hotel","india","juliet");

    final static long LIMIT = 100_000_000L;

    /**
     * 1. Filters every name for length >= 6
     * 2. Makes it to Upper case
     * 3. Collects using a List
     * 4. sout it
     */
    public static void filterAndUpperCase(){
        System.out.println(
                names.stream()
                     .filter(s -> s.length() >= 6)
                     .map(s -> s.toUpperCase())
                     .collect(Collectors.toList())
        );
    }

    /**
     * 0. Peek the value
     * 1. Filters every name for length >= 6
     * 2. Peek the value
     * 3. Makes it to Upper case
     * 4. Collects using a List
     * 5. sout it
     */
    public static void filterAndUpperCaseWithPeek(){
        System.out.println(
                names.stream()
                        .peek(s -> System.out.printf("###%s###%n", s))
                        .filter(s -> s.length() >= 6)
                        // peeks the value but doesn't modify it.
                        // PS: Peek can be applied anywhere in the intermediate operations
                        .peek(s -> System.out.printf("***%s***%n", s))
                        .map(s -> s.toUpperCase())
                        .collect(Collectors.toList())
        );
    }

    /**
     * Sorts the names in the reverse order (alpha num)
     */
    public static void sorted_SimpleDemo(){
        System.out.println(
                names.stream()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList())
        );
    }

    /**
     * Sorts the names based on the length of String
     */
    public static void sorted_ByStringLengthDemo(){
        System.out.println(
                names.stream()
                        .sorted(Comparator.comparingInt(s -> s.length())) //TODO:Read Comparator Interface methods
                        .collect(Collectors.toList())
        );
    }

    /**
     * Sorts the names based on the length of String and for the ones with same length does reverse sort on alpha num
     */
    public static void sorted_ByStringLengthAndForEqualLengthSortInReverseOrderDemo(){
        System.out.println(
                names.stream()
                        //Composing Comparators with thenComparing.
                        //PS: s has type inference difficulty so it needs an explicit decleration of String
                        .sorted(Comparator.comparingInt((String s) -> s.length())
                                .thenComparing(Comparator.reverseOrder()))
                        .collect(Collectors.toList())
        );
    }

    /**
     * Streams are different for Objects and primitives
     * streams are available for int, long, double
     * PS: Not all primitives have Streams
     * TODO: Read IntStream api
     */
    public static void stream_IntStreamDemo(){
        System.out.println(
                IntStream.rangeClosed(0, 20) //Creates a stream. TODO: Check range()
                         //.parallel() //PS: Any stream can be made parallel by adding this
                         .filter(i -> (i % 2) == 0)
                         .boxed() //Boxes primitive to Wrapper Class
                         .collect(Collectors.toList())
        );
    }

    /**
     * Encountered Vs Processing Order explained
     */
    public static void stream_EncounterVsProcessingOrderDemo(){

        //This has to be synchronized if not there will be a problem as the processing threads are in parallel
        List<Integer> list1 = Collections.synchronizedList(new ArrayList<>());


        List<Integer> list2 =
                IntStream.range(0, 20)
                         .parallel()
                         .boxed()
                         .peek(i -> list1.add(i))
                         .collect(Collectors.toList());

        //The threads may be possessed in any order. As we peek as its being processed
        //This is "Processing Order"
        System.out.println(list1); //[12, 14, 13, 11, 10, 2, 4, 3, 1, 0, 19, 18, 8, 6, 17, 16, 5, 15, 7, 9]

        //The collect will try to match the ordering of the source stream, though processed in parallel
        //This source ordering is "Encountered Order"
        System.out.println(list2); //[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
    }

    /**
     * Simple Parallel Stream example in which every thread is made to sleep 500 milli secs
     */
    public static void streamParallel_Simple(){
        System.out.println(
                names.parallelStream() //In normal Stream it will take 5+ secs to complete
                     .peek(s -> Util.sleepUninterrupted(500L))
                     .collect(Collectors.toList())
        );
    }

    public static void streamParallel_SumSetOfIntsDemo(){

        ////////////////////////////////////////////////////////
        // 1. Old school way of sum, using a mutation variable
        //      OLD APPROACH -> sequential
        ////////////////////////////////////////////////////////
        /*
        long sum = 0L;
        for (int i = 1; i <= LIMIT; i++)
            sum += i;
        System.out.println("sum = " + sum);
        */

        ////////////////////////////////////////////////////////
        // 2. Streams still with mutation variable
        //      BAD CODE -> wont work if done in parallel
        //      Its a bad hack used by programmers
        ////////////////////////////////////////////////////////
        //long sum = 0L; //Cant use this. Local [effectively] non-final cant be used in lambdas / inner class
        /*
        long[] sum = {0L}; // This is a BAD HACK used by programmers to cheat compiler
        LongStream.rangeClosed(1, LIMIT)
                  //.parallel() //Check with this enabled
                  //.forEach(i -> sum += i);
                  .forEach(i -> sum[0] += i);
        System.out.println("sum = " + sum[0]);
        */

        ////////////////////////////////////////////////////////
        // 3. Streams still with mutation variable
        // LESS BAD CODE -> will work in parallel by dead slow
        ////////////////////////////////////////////////////////
        /*
        AtomicLong sum = new AtomicLong(0L); // Is Synchronized and makes the program slow even in parallel
        LongStream.rangeClosed(1, LIMIT)
                  .parallel()
                  .forEach(i -> sum.addAndGet(i));
        System.out.println("sum = " + sum.get());
        */


        ////////////////////////////////////////////////////////
        // 3. Streams still with mutation variable
        // LESS BAD CODE -> will work in parallel by dead slow
        ////////////////////////////////////////////////////////
        /*
        AtomicLong sum = new AtomicLong(0L); // Is Synchronized and makes the program slow even in parallel
        LongStream.rangeClosed(1, LIMIT)
                  .parallel()
                  .forEach(i -> sum.addAndGet(i));
        System.out.println("sum = " + sum.get());
        */

        ///////////////////////////////////////////////////////////////////////////////
        // 4. Streams NO mutation variable
        //    GOOD IDEA -> No Mutation, Runs Parallel
        // PS: It is addition by reduction, works well for any associative operations
        ///////////////////////////////////////////////////////////////////////////////

        System.out.println(
            LongStream.rangeClosed(1, LIMIT)
                      .parallel()
                      .reduce(0, (i, j) -> i + j) //This can be simply replaced with .sum() both one and the same
        );

    }


    public static void main(String[] args) {
        long startTime = System.nanoTime();
        //filterAndUpperCase();
        //filterAndUpperCaseWithPeek();
        //sorted_SimpleDemo();
        //sorted_ByStringLengthDemo();
        //sorted_ByStringLengthAndForEqualLengthSortInReverseOrderDemo();
        //stream_IntStreamDemo();
        //streamParallel_Simple();
        //stream_EncounterVsProcessingOrderDemo();
        streamParallel_SumSetOfIntsDemo();

        long duration = System.nanoTime() - startTime;
        System.out.printf(" ==> [ Time Taken = %.3f secs ] %n", (double) duration / Math.pow(10, 9));
    }
}
