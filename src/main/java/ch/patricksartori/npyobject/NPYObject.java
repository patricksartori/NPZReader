package ch.patricksartori.npyobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@ToString
@Getter
public class NPYObject<T> {
    private String descr;
    private int[] shape;
    private List<T> data;


    public NPYObject(String descr, int[] shape, List<T> data) {
        this.descr = descr;
        this.shape = shape;
        this.data = data;
    }
}
