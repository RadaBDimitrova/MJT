package bg.sofia.uni.fmi.mjt.football;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.football.Position.RM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FootballPlayerAnalyzerTest {
    @Test
    void testGetAllPlayers() {
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
        assertEquals(7, analyzer.getAllPlayers().size());
    }

    @Test
    void testGetAllNationalities() {
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
        Set<String> expectedSet = Set.of("Argentina", "Denmark", "France", "Italy", "Senegal", "Netherlands");
        Set<String> actualSet = analyzer.getAllNationalities();
        assertEquals(expectedSet, actualSet);
    }

    @Test
    void getHighestPaidPlayerByNationality() {
        String dataset = """
                name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
                L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
                C. Eriksen;Christian  Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right
                P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right
                L. Insigne;Lorenzo Insigne;6/4/1991;27;162.56;59;LW,ST;Italy;88;88;62000000;165000;Right
                K. Koulibaly;Kalidou Koulibaly;6/20/1991;27;187.96;88.9;CB;Senegal;88;91;60000000;135000;Right
                V. van Dijk;Virgil van Dijk;7/8/1991;27;193.04;92.1;CB;Netherlands;88;90;59500000;215000;Right
                K. Mbappé;Kylian Mbappé;12/20/1998;20;152.4;73;RW,ST,RM;France;88;95;81000000;100000;Right
                S. Agüero;Sergio Leonel Agüero del Castillo;6/2/1988;30;172.72;69.9;ST;Argentina;89;89;64500000;300000;Right
                M. Neuer;Manuel Neuer;3/27/1986;32;193.04;92.1;GK;Germany;89;89;38000000;130000;Right
                E. Cavani;Edinson Roberto Cavani Gómez;2/14/1987;32;185.42;77.1;ST;Uruguay;89;89;60000000;200000;Right
                Sergio Busquets;Sergio Busquets i Burgos;7/16/1988;30;187.96;76.2;CDM,CM;Spain;89;89;51500000;315000;Right
                T. Courtois;Thibaut Courtois;5/11/1992;26;198.12;96.2;GK;Belgium;89;90;53500000;240000;Left
                M. ter Stegen;Marc-André ter Stegen;4/30/1992;26;187.96;84.8;GK;Germany;89;92;58000000;240000;Right
                A. Griezmann;Antoine Griezmann;3/21/1991;27;175.26;73;CF,ST;France;89;90;78000000;145000;Left
                M. Salah;Mohamed  Salah Ghaly;6/15/1992;26;175.26;71.2;RW,ST;Egypt;89;90;78500000;265000;Left
                P. Dybala;Paulo Bruno Exequiel Dybala;11/15/1993;25;152.4;74.8;CAM,RW;Argentina;89;94;89000000;205000;Left
                M. Škriniar;Milan Škriniar;2/11/1995;24;187.96;79.8;CB;Slovakia;86;93;53500000;89000;Right
                Fernandinho;Fernando Luiz Rosa;5/4/1985;33;152.4;67.1;CDM;Brazil;87;87;20500000;200000;Right
                G. Higuaín;Gonzalo Gerardo Higuaín;12/10/1987;31;185.42;88.9;ST;Argentina;87;87;48500000;205000;Right
                """;

        FootballPlayerAnalyzer analyzer = new FootballPlayerAnalyzer(new StringReader(dataset));
        Player highestPaidPlayerByNationality = analyzer.getHighestPaidPlayerByNationality("Argentina");
        assertNotNull(highestPaidPlayerByNationality);
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.getHighestPaidPlayerByNationality(null));
        assertEquals("L. Messi", highestPaidPlayerByNationality.name());

        assertThrows(NoSuchElementException.class,
                () -> analyzer.getHighestPaidPlayerByNationality("Bulgaria"));
    }

    @Test
    void testGroupByPosition() {
        String dataset = """
                name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
                L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;RM;Argentina;94;94;110500000;565000;Left
                C. Eriksen;Christian Dannemann Eriksen;2/14/1992;27;154.94;76.2;RM;Denmark;88;89;69500000;205000;Right
                """;
        String p1 = "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;RM;Argentina;94;94;110500000;565000;Left";
        String p2 = "C. Eriksen;Christian Dannemann Eriksen;2/14/1992;27;154.94;76.2;RM;Denmark;88;89;69500000;205000;Right";
        Player Messi = Player.of(p1);
        Player Eriksen = Player.of(p2);
        FootballPlayerAnalyzer analyzer = new FootballPlayerAnalyzer(new StringReader(dataset));
        Map<Position, Set<Player>> result = analyzer.groupByPosition();
        Map<Position, Set<Player>> expected = new HashMap<>();
        expected.put(RM, Set.of(Messi, Eriksen));
        assertEquals(expected, result);
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudget() {
        String dataset = """
                name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
                L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;RM;Argentina;94;94;110500000;565000;Left
                C. Eriksen;Christian Dannemann Eriksen;2/14/1992;27;154.94;76.2;RM;Denmark;88;89;69500000;205000;Right
                """;
        String p1 = "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;RM;Argentina;94;94;110500000;565000;Left";
        String p2 = "C. Eriksen;Christian Dannemann Eriksen;2/14/1992;27;154.94;76.2;RM;Denmark;88;89;69500000;205000;Right";
        Player Messi = Player.of(p1);
        Player Eriksen = Player.of(p2);
        FootballPlayerAnalyzer analyzer = new FootballPlayerAnalyzer(new StringReader(dataset));
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.getTopProspectPlayerForPositionInBudget(null, 300));
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.getTopProspectPlayerForPositionInBudget(RM, -2));
        assertEquals(Optional.of(Eriksen), analyzer.getTopProspectPlayerForPositionInBudget(RM, 69500000));
        assertEquals(Optional.of(Eriksen), analyzer.getTopProspectPlayerForPositionInBudget(RM, 110500000));
        assertEquals(Optional.empty(), analyzer.getTopProspectPlayerForPositionInBudget(RM, 695000));
    }

    @Test
    void testGetSimilarPlayers() {
        String dataset = """
                name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
                L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
                C. Eriksen;Christian Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right
                P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right
                L. Insigne;Lorenzo Insigne;6/4/1991;27;162.56;59;LW,ST;Italy;88;88;62000000;165000;Right
                K. Koulibaly;Kalidou Koulibaly;6/20/1991;27;187.96;88.9;CB;Senegal;88;91;60000000;135000;Right
                V. van Dijk;Virgil van Dijk;7/8/1991;27;193.04;92.1;CB;Netherlands;88;90;59500000;215000;Right
                """;
        String p1 = "C. Eriksen;Christian Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right";
        Player Eriksen = Player.of(p1);
        String p2 = "P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right";
        Player Pogba = Player.of(p2);
        FootballPlayerAnalyzer analyzer = new FootballPlayerAnalyzer(new StringReader(dataset));
        Set<Player> expected = Set.of(Eriksen, Pogba);
        Set<Player> result = analyzer.getSimilarPlayers(Eriksen);
        assertEquals(expected, result);
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.getSimilarPlayers(null));
    }

    @Test
    void testGetPlayersByFullNameKeyword() {
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

        String p1 = "L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left";
        String p2 = "C. Eriksen;Christian Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right";
        String p3 = "L. Insigne;Lorenzo Insigne;6/4/1991;27;162.56;59;LW,ST;Italy;88;88;62000000;165000;Right";
        Player Messi = Player.of(p1);
        Player Eriksen = Player.of(p2);
        Player Insigne = Player.of(p3);
        FootballPlayerAnalyzer analyzer = new FootballPlayerAnalyzer(new StringReader(dataset));
        Set<Player> expected = new LinkedHashSet<>();
        expected.add(Messi);
        expected.add(Eriksen);
        expected.add(Insigne);
        Set<Player> result = analyzer.getPlayersByFullNameKeyword("ne");
        assertEquals(expected, result);
    }
}
