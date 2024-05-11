package top.histevehu.srpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 测试用api实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCountAddObject implements Serializable {

    @Serial
    private static final long serialVersionUID = -1057311712191051644L;

    private Integer num;
    private Integer offset;
}
