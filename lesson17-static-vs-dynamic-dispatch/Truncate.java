class Truncate {
    protected final double nr;

    public Truncate(double nr) { this.nr = nr; }

    // open by default => virtual
    public double cut() { return (int) nr; }
    public double cut(int decimals) {
        int d = 1;
        while(decimals-- > 0) d *= 10;
        return ((double) ((int) (nr * d))) / d;
    }
}

class Round extends Truncate {

    public Round(double nr) { super(nr); }

    // override by default
    public double cut() { return (int) (nr + 0.5); }
    public double cut(int decimals) {
        int d = 1;
        while (decimals-- > 0) { d *= 10; }
        return ((int) ((nr * d) + 0.5)) / (double) d;
    }
}