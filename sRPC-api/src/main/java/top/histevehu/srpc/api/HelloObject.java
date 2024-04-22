package top.histevehu.srpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 测试用api实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelloObject implements Serializable {

    private Integer id;
    private String message;

}
