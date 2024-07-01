package ch.patricksartori.npyobject;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@ToString
@Getter
@AllArgsConstructor
public class NPYObject<T> {
    private String name;
    private String descr;
    private int[] shape;
    private List<T> data;


}
