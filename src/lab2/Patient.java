package lab2;

public class Patient {
    public int id;
    public String name;
    public boolean isSick = false;
    public String sickness;
    public String treatment;

    public String toString() {
        String res = "ID: " + id + " name: " + name;
        if (isSick)
            res += " currently sick";
        if (sickness != null && !sickness.equals(""))
            res += " sickness: " + sickness;
        if (treatment != null && !treatment.equals(""))
            res += " treatment: " + treatment;
        return res;
    }
}
