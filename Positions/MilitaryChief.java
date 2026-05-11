public class MilitaryChief implements Positions {

    private MilitaryChief() {}
    private static final MilitaryChief MC = new MilitaryChief();
    public static MilitaryChief getObject() {
        return MC;
    }

    @Override
    public void work() {
    }

    @Override
    public String getPositionName() {
        return "Military chief";
    }
}
