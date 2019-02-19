package edu.acc.j2ee.hubbub;

public class DaoException extends RuntimeException {
    public DaoException(Exception inner) {
        this.initCause(inner);
    }
}
