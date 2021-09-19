public class Bin {
    Float interval;
    Integer count;
    Float cumProbability;

    public Bin (float interval, int count, float cumProbability){
        this.interval = interval;
        this.count = count;
        this.cumProbability = cumProbability;
    }

    //Getters
    public Float getInterval() {
        return interval;
    }

    public Integer getCount() {
        return count;
    }

    public Float getCumProbability() {
        return cumProbability;
    }

    //Setters
    public void setInterval(Float interval) {
        this.interval = interval;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setCumProbability(Float cumProbability) {
        this.cumProbability = cumProbability;
    }
}
