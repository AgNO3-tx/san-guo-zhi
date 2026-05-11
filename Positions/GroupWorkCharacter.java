import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class GroupWorkCharacter implements Comparable<GroupWorkCharacter> {
    private String Name;
    private Positions Position = null;//职位
    private Army army = null;//兵种
    private char grade;
    {
        int tmep = GroupWorkCharacter.totalCapabiliityValue(this);
        if (tmep >= 250){
            grade = 'S';
        }else if (tmep >= 220){
            grade = 'A';
        }else if (tmep >= 190){
            grade = 'B';
        }else {
            grade = 'C';
        }
    }

    private int Politics;//政治
    private int Leadership;//领导力
    private int Force;//武力
    private int Intelligence;//智力
    private int Health;//生命值

    public GroupWorkCharacter() {
    }
    public GroupWorkCharacter(String Nam, Positions Pos, Army army, int Politics, int Leadership, int Force, int Intelligence, int Health) {
        this.Name = Nam;
        this.Position = Pos;
        this.army = army;
        this.Politics = Politics;
        this.Leadership = Leadership;
        this.Force = Force;
        this.Intelligence = Intelligence;
        this.Health = Health;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Positions getPosition() {
        return Position;
    }

    public void setPosition(Positions position) {
        Position = position;
    }

    public Army getArmy() {
        return army;
    }

    public void setArmy(Army army) {
        this.army = army;
    }

    public char getGrade() {
        return grade;
    }

    public void setGrade(char grade) {
        this.grade = grade;
    }

    public int getPolitics() {
        return Politics;
    }

    public void setPolitics(int politics) {
        Politics = politics;
    }

    public int getLeadership() {
        return Leadership;
    }

    public void setLeadership(int leadership) {
        Leadership = leadership;
    }

    public int getForce() {
        return Force;
    }

    public void setForce(int force) {
        Force = force;
    }

    public int getIntelligence() {
        return Intelligence;
    }

    public void setIntelligence(int intelligence) {
        Intelligence = intelligence;
    }

    public int getHealth() {
        return Health;
    }

    public void setHealth(int health) {
        Health = health;
    }

    public static Positions getPositions(int Force, int Intelligence) {
        if(Force <= Intelligence) {
            return new MilitaryDepartment();
        }else{
            return new HomeDepartment();
        }
    }

    //按照属性对人物进行排序
    public static  String[] sortGroupWorkCharacters(GroupWorkCharacter[] gwcArray) {
        System.out.println("imput datatype which you what to seek"+'\n'+
                "1：Politics"+'\n'+
                "2: Leadership"+'\n'+
                "3: Force"+'\n'+
                "4: Intelligence"+'\n'+
                "5: Health");

        switch (new Scanner(System.in).nextInt()) {
            case 1:
                //比较政治力，按政治力排序
                Arrays.sort(gwcArray,(GroupWorkCharacter o1, GroupWorkCharacter o2) -> o2.getPolitics() - o1.getPolitics());
                break;
            case 2:
                //比较领导力，按领导力排序
                Arrays.sort(gwcArray,(o1,o2) -> o2.getLeadership() - o1.getLeadership());
                break;
            case 3:
                //比较武力，按武力排序
                Arrays.sort(gwcArray,(o1,o2) -> o2.getForce() - o1.getForce());
                break;
            case 4:
                //比较智力，按智力排序
                Arrays.sort(gwcArray, (o1, o2) -> o2.getIntelligence() - o1.getIntelligence());
                break;
            case 5:
                //比较生命值，按生命值排序
                Arrays.sort(gwcArray, (o1, o2) -> o2.getHealth() - o1.getHealth());
                break;
        }

        String[] returnArray = new String[gwcArray.length];
        for (int i = 0; i < gwcArray.length; i++) {
            returnArray[i] = gwcArray[i].getName();
        }
        return returnArray;
    }

    private static int totalCapabiliityValue(GroupWorkCharacter g) {
        return g.getPolitics() + g.getLeadership() + g.getIntelligence() + g.getHealth() + g.getForce();
    }


    //有问题等待复写
    /*
    public static GroupWorkCharacter BinarySearch(GroupWorkCharacter[] gwcArray,int key) {
        GroupWorkCharacter.sortGroupWorkCharacters(gwcArray);
        int index = 0;
        return null;
    }
     */



    @Override
    public String toString() {
        return "Name: " + Name + '\n' +
                "Position: " + Position.getPositionName() + '\n' +
                "Army: " + army + '\n' +
                "Politics:" + Politics + '\n' +
                "Leadership:" + Leadership + '\n' +
                "Force:" + Force + '\n' +
                "Intelligence" + Intelligence + '\n'+
                "Health:" + Health;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GroupWorkCharacter)) return false;
        GroupWorkCharacter that = (GroupWorkCharacter) o;
        return getGrade() == that.getGrade() && getPolitics() == that.getPolitics() && getLeadership() == that.getLeadership() && getForce() == that.getForce() && getIntelligence() == that.getIntelligence() && getHealth() == that.getHealth() && Objects.equals(getName(), that.getName()) && Objects.equals(getPosition(), that.getPosition()) && Objects.equals(getArmy(), that.getArmy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPosition(), getArmy(), getGrade(), getPolitics(), getLeadership(), getForce(), getIntelligence(), getHealth());
    }

    @Override
    public int compareTo(GroupWorkCharacter obj) {
        int i = 0;
        int[] arr = {this.Politics-obj.Politics, this.Leadership-obj.Leadership, this.Force-obj.Force, this.Intelligence-obj.Intelligence, this.Health-obj.Health};
        return arr[i];
    }


}
