package Common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@Builder
public class SampleObject implements Serializable {

    private static final long serialVersionUID = 123456L;

    private long id;
    private String name;
    private int age;
    private boolean isMale;

}
