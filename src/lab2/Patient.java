package lab2;

public class Patient {
    public static final int ONLY_DOC_TREATMENT = 2;
    public static final String[] TREATMENTS = new String[]{"Procedures", "Drugs", "Operation"};

    public int id;
    public String name;
    public boolean isSick = false;
    public String sickness;
    public String treatment;
    public String sicknessHistory;

    public String toString() {
        String res = "ID: " + id + " name: " + name + ";";
        if (sicknessHistory != null && !sicknessHistory.equals(""))
            res += " sickness history: " + sicknessHistory+ ";";
        if (isSick)
            res += " currently sick;";
        if (sickness != null && !sickness.equals(""))
            res += " sickness: " + sickness+ ";";
        if (treatment != null && !treatment.equals(""))
            res += " treatment: " + treatment+ ";";
        return res;
    }
}
