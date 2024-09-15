package top.histevehu.srpc.testClient.Benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RpcBenchmarkRunner {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RpcBenchmark.class.getSimpleName())
                .forks(1)  // JVM数
                .threads(16)  // 线程数
                .warmupIterations(5)  // 预热次数
                .measurementIterations(10)  // 实际测试次数
                .verbosity(VerboseMode.NORMAL)  // 输出等级
                .output("benchmark_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".log")
                .build();
        new Runner(opt).run();
    }
}