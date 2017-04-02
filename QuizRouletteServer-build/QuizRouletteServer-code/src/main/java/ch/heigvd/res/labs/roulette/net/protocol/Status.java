package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * Created by sydney on 31.03.17.
 */
public enum Status {
    Success("sucess"),
    Error("error");

    private String status;
    Status(String status) {
        this.status = status;
    }

    public String toString() {
        return status;
    }
}