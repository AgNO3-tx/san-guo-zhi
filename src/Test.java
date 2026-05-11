import java.util.Arrays;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        //创造人物并录入数组中
        GroupWorkCharacter g1 = new GroupWorkCharacter("Sun Quan",WuMonarch.getObject(),new Cavalry(),77,98,96,72,95);
        GroupWorkCharacter g2 = new GroupWorkCharacter("Zhou Yu",MilitaryChief.getObject(),new Cavalry(),80,86,80,97,90);
        GroupWorkCharacter g3 = new GroupWorkCharacter("Zhang Zhao",DirectorOfHomeAffairs.getObject(),new Archer(),99,80,22,89,60);
        GroupWorkCharacter g4 = new GroupWorkCharacter("Xu Sheng",GroupWorkCharacter.getPositions(90,72),new Archer(),40,78,90,72,94);
        GroupWorkCharacter g5 = new GroupWorkCharacter("Zhu Ge Jin",GroupWorkCharacter.getPositions(63,88),new Archer(),82,61,63,88,71);
        GroupWorkCharacter g6 = new GroupWorkCharacter("Lu Su",GroupWorkCharacter.getPositions(43,84),new Infantry(),88,87,43,84,53);
        GroupWorkCharacter g7 = new GroupWorkCharacter("Tai Shi Ci",GroupWorkCharacter.getPositions(96,43),new Cavalry(),33,81,96,43,97);
        GroupWorkCharacter g8 = new GroupWorkCharacter("Xiao Qiao",GroupWorkCharacter.getPositions(42,89),new Infantry(),77,52,42,89,34);
        GroupWorkCharacter g9 = new GroupWorkCharacter("Da Qiao",GroupWorkCharacter.getPositions(39,90),new Cavalry(),62,62,39,90,41);
        GroupWorkCharacter g10 = new GroupWorkCharacter("Zhou Tai",GroupWorkCharacter.getPositions(92,72),new Infantry(),43,89,92,72,99);
        GroupWorkCharacter g11 = new GroupWorkCharacter("Gan Ning",GroupWorkCharacter.getPositions(98,45),new Archer(),23,92,98,45,97);
        GroupWorkCharacter g12 = new GroupWorkCharacter("Lu Meng",GroupWorkCharacter.getPositions(70,93),new Cavalry(),83,77,70,93,88);
        GroupWorkCharacter g13 = new GroupWorkCharacter("Huang Gai",GroupWorkCharacter.getPositions(83,72),new Infantry(),42,98,83,72,89);
        GroupWorkCharacter[] arr = new GroupWorkCharacter[]{g1,g2,g3,g4,g5,g6,g7,g8,g9,g10,g11,g12,g13};

        System.out.println(g1.getGrade());
    }
}