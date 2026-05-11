public class DirectorOfHomeAffairs implements Positions {

    private DirectorOfHomeAffairs() {}
    private static final DirectorOfHomeAffairs DOHA = new DirectorOfHomeAffairs();
    public static DirectorOfHomeAffairs getObject() {
        return DOHA;
    }

    @Override
    public void work() {
    }

    @Override
    public String getPositionName() {
        return "Director of home affairs";
    }
}
