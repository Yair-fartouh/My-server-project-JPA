package ExcelReader;

public class Time {

    private int hour;
    private int minute;
    private double km;

    public Time(int hour, int minute, double km) {
        this.hour = hour;
        this.minute = minute;
        this.km = km;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }

}
