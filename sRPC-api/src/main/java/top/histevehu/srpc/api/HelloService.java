package top.histevehu.srpc.api;

/**
 * 测试用api接口
 */
public interface HelloService {

    default String hello(HelloObject object) {
        return object.toString();
    }

    default String hello() {
        return "Hello World";
    }
}
