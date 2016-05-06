package com.herthrone.stats;

/**
 * Created by yifengliu on 5/5/16.
 */
public class Value {

    private int val;

    public Value(final int val) {
        this.val = val;
    }

    public Value() {
        this(0);
    }

    public void reset() {
        this.val = 0;
    }

    public void increase(final int gain) {
        this.val += gain;
    }

    public void decrease(final int loss) {
        this.val -= loss;
    }

    public int getVal() {
        return this.val;
    }

    public void setTo(final int val) {
        this.val = val;
    }
}
