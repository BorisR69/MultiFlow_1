import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        int max = 0;
        for (int i = 0; i < texts.length; i++) texts[i] = generateText("aab", 30_000);

        long startTs = System.currentTimeMillis(); // start time
        List<Future<Integer>> listFuture = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for (String text : texts) {
            // Создаем задачу для запуска в потоке
            Callable<Integer> run = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(Thread.currentThread().getName() + " " + text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };
            Future<Integer> future = threadPool.submit(run); // запуск задачи на выполнение в пуле потоков
            listFuture.add(future); // добавление задачи в список
        }

        for (Future<Integer> futureTask : listFuture) { // Поиск максимального интервала значений
            int curMax = futureTask.get();
            if (max < curMax) max = curMax;
        }
        System.out.println("Максимальный интервала значений = " + max);
        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
        threadPool.shutdown(); // закрытие пула потоков
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

}