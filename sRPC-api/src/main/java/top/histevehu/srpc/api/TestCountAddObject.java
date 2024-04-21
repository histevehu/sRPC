package top.histevehu.srpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 测试用api实体
 */
@Data
@AllArgsConstructor
public class TestCountAddObject implements Serializable {

    private Integer num;
    private Integer offset;
}
