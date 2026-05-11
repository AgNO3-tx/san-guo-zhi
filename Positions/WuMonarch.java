public class WuMonarch implements Positions {
    private WuMonarch() {}
    private static final WuMonarch Wum = new WuMonarch();
    public static WuMonarch getObject() {
        return Wum;
    }
    @Override
    public void work() {

    }

    @Override
    public String getPositionName() {
        return "GroupWorkCharacter.Positions.WuMonarch";
    }
}

