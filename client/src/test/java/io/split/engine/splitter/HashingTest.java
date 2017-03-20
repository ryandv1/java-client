package io.split.engine.splitter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.*;

/**
 * Created by adilaijaz on 1/18/16.
 */
public class HashingTest {

    @Test
    public void numberOverflow() {
        int seed = (int) System.currentTimeMillis();

        List<String> keys = reallyLargeKeys(2000000, 10);

        MyHash hash = new MyHash.SeededNaturalHash();
        for (String key : keys) {
            hash.hash(seed, key);
        }
    }

    @Test
    public void spreadBySeedTest() {
        int seed1 = (int) System.currentTimeMillis();
        int seed2 = seed1 + 1;
        List<String> keys = randomUUIDs(216553);

        spreadBySeed(seed1, seed2, new MyHash.Murmur32Hash(), keys);
        spreadBySeed(seed1, seed2, new MyHash.XorNaturalHash(), keys);
    }

    @Test
    public void collisionTestForSequential() {

        int seed = (int) System.currentTimeMillis();

        List<String> sequentialKeys = sequentialIds(200000);
        collisionTest(seed, new MyHash.Murmur32Hash(), sequentialKeys);
        collisionTest(seed, new MyHash.SeededNaturalHash(), sequentialKeys);
        collisionTest(seed, new MyHash.XorNaturalHash(), sequentialKeys);

    }

    @Test
    public void bucketTestForSequential() {

        int seed = (int) System.currentTimeMillis();

        List<String> sequentialKeys = mshIds();
        bucketTest(seed, new MyHash.Murmur32Hash(), sequentialKeys);
        bucketTest(seed, new MyHash.SeededNaturalHash(), sequentialKeys);
        bucketTest(seed, new MyHash.XorNaturalHash(), sequentialKeys);

    }

    @Test
    public void collisionTestForRandom() {

        int seed = (int) System.currentTimeMillis();

        List<String> sequentialKeys = randomUUIDs(200000);
        collisionTest(seed, new MyHash.Murmur32Hash(), sequentialKeys);
        collisionTest(seed, new MyHash.SeededNaturalHash(), sequentialKeys);
        collisionTest(seed, new MyHash.XorNaturalHash(), sequentialKeys);

    }


    private List<String> randomUUIDs(int size) {
        List<String> bldr = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
            bldr.add(UUID.randomUUID().toString());
        }
        return bldr;
    }

    private List<String> sequentialIds(int size) {
        List<String> bldr = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
            bldr.add("" + i);
        }
        return bldr;
    }

    private List<String> mshIds() {
        List<String> bldr = Lists.newArrayList();
        for (int i = 28243; i <= 28267; i++) {
            bldr.add("" + i);
        }
        return bldr;
    }

    private List<String> reallyLargeKeys(int keySize, int numKeys) {
        List<String> bldr = Lists.newArrayList();

        for (int i = 0; i < numKeys; i++) {
            bldr.add(RandomStringUtils.randomAlphanumeric(keySize));
        }

        return bldr;
    }

    private void collisionTest(int seed, MyHash hash, List<String> keys) {
        int collisions = 0;
        long durationSum = 0;

        Set<Integer> hashes = Sets.newHashSet();

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            long start = System.nanoTime();
            int keyHash = hash.hash(seed, key);
            durationSum += (System.nanoTime() - start);

            if (!hashes.add(keyHash)) {
                collisions++;
            }
        }

        System.out.println(hash + " collisions: " + collisions + " percentage: " + (100f * collisions / keys.size()));
        System.out.println(hash + " time: " + durationSum / keys.size() + " ns");
    }

    private void bucketTest(int seed, MyHash hash, List<String> keys) {
        List<Integer> buckets = Lists.newArrayList();

        int[] ranges =  new int[10];

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            int keyHash = hash.hash(seed, key);
            int bucket = Splitter.bucket(keyHash);
            buckets.add(bucket);
            ranges[(bucket-1)/10]++;
        }

        System.out.println(Arrays.toString(ranges));
    }

    private void spreadBySeed(int seed1, int seed2, MyHash hash, List<String> keys) {

        BitSet bitset = new BitSet();

        int i = 0;
        Iterator<String> iter = keys.iterator();
        while (iter.hasNext()) {
            String key = iter.next();

            if (hash.hash(seed1, key) == hash.hash(seed2, key)) {
                bitset.set(i);
            }

            i++;
        }

        int collisions = bitset.cardinality();
        System.out.println(hash + " collisions " + collisions + " percentage: " + (100f * collisions / keys.size()));
    }

    private void spreadBySeedBucket(int seed1, int seed2, MyHash hash, List<String> keys, int bucketRatio) {

        int collisions = 0;
        Iterator<String> iter = keys.iterator();
        while (iter.hasNext()) {
            String key = iter.next();

            int bucket1 = Splitter.bucket(hash.hash(seed1, key)) / bucketRatio;
            int bucket2 = Splitter.bucket(hash.hash(seed2, key)) / bucketRatio;
            if (bucket1 == bucket2) {
                collisions++;
            }
        }

        System.out.println(hash + " collisions " + collisions + " percentage: " + (100f * collisions / keys.size()));
    }

    @Test
    public void twilioTest() {
        Random r = new Random();
        int seed1 = r.nextInt();
        int seed2 = Integer.reverse(seed1);

        List<String> randomUUIDs = randomUUIDs(216553);
        List<String> sequentialIDs = sequentialIds(216553);
        List<String> mshIds = mshIds();

        System.out.println("Bucket Spread by Seed Random UUIDs");
        spreadBySeedBucket(seed1, seed2, new MyHash.Murmur32Hash(), randomUUIDs, 10);
        spreadBySeedBucket(seed1, seed2, new MyHash.SeededNaturalHash(), randomUUIDs, 10);

        System.out.println("Bucket Spread by Seed Sequential UUIDs");
        spreadBySeedBucket(seed1, seed2, new MyHash.Murmur32Hash(), sequentialIDs, 10);
        spreadBySeedBucket(seed1, seed2, new MyHash.SeededNaturalHash(), sequentialIDs, 10);

        System.out.println("Bucket Spread by Seed MSH UUIDs");
        spreadBySeedBucket(seed1, seed2, new MyHash.Murmur32Hash(), mshIds, 10);
        spreadBySeedBucket(seed1, seed2, new MyHash.SeededNaturalHash(), mshIds, 10);
    }

}
