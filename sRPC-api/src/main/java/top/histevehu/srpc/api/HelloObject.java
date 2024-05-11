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
@NoArgsConstructor
@AllArgsConstructor
public class HelloObject implements Serializable {

    @Serial
    private static final long serialVersionUID = -8367572636259228177L;

    private Integer id;
    private String message;

}
