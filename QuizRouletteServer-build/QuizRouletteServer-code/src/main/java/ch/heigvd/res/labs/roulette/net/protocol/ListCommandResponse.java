package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.List;

/**
 * Created by Blue Spring on 31.03.2017.
 */
public class ListCommandResponse {

    private List<Student> list;

    public ListCommandResponse(List<Student> list) {
        this.list = list;
    }

    public List<Student> getList() {
        return list;
    }
}
