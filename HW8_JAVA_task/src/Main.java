import bg.sofia.uni.fmi.mjt.football.FootballPlayerAnalyzer;
import bg.sofia.uni.fmi.mjt.football.Player;
import bg.sofia.uni.fmi.mjt.football.Position;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.football.Position.RM;

public class Main {
    public static void main(String[] args) {


        String dataset = """
                name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
                L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
                C. Eriksen;Christian Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right
                P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right
                L. Insigne;Lorenzo Insigne;6/4/1991;27;162.56;59;LW,ST;Italy;88;88;62000000;165000;Right
                K. Koulibaly;Kalidou Koulibaly;6/20/1991;27;187.96;88.9;CB;Senegal;88;91;60000000;135000;Right
                V. van Dijk;Virgil van Dijk;7/8/1991;27;193.04;92.1;CB;Netherlands;88;90;59500000;215000;Right
                K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right
                """;

        FootballPlayerAnalyzer analyzer = new FootballPlayerAnalyzer(new StringReader(dataset));
        Set<Player> result = analyzer.getPlayersByFullNameKeyword("ne");
        System.out.println("All Nationalities: " + result);


    }
}