package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MJTSpaceScannerTest {
    private static final int KEY_SIZE = 128;
    private static final String CSV_DATA =
            "Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket,\" Rocket\",Status Mission" +
                    System.lineSeparator() +
                    "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\"," +
                    "Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,\"50.0 \",Success" +
                    System.lineSeparator() +
                    "1,CASC,\"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China\",\"Thu Aug 06, 2020\"" +
                    ",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,\"29.75 \",Success" +
                    System.lineSeparator() +
                    "2,SpaceX,\"Pad A, Boca Chica, Texas, USA\",\"Tue Aug 04, 2020\"" +
                    ",Starship Prototype | 150 Meter Hop,StatusActive,,Success" +
                    System.lineSeparator() +
                    "3,Roscosmos,\"Site 200/39, Baikonur Cosmodrome, Kazakhstan\",\"Thu Jul 30, 2020\"" +
                    ",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,\"65.0 \",Success" +
                    System.lineSeparator() +
                    "4,ULA,\"SLC-41, Cape Canaveral AFS, Florida, USA\",\"Thu Jul 30, 2020\"" +
                    ",Atlas V 541 | Perseverance,StatusActive,\"145.0 \",Success" +
                    System.lineSeparator() +
                    "5,CASC,\"LC-9, Taiyuan Satellite Launch Center, China\",\"Sat Jul 25, 2020\"" +
                    ",\"Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1\",StatusActive,\"64.68 \",Success" +
                    System.lineSeparator() +
                    "6,Roscosmos,\"Site 31/6, Baikonur Cosmodrome, Kazakhstan\",\"Thu Jul 23, 2020\"" +
                    ",Soyuz 2.1a | Progress MS-15,StatusActive,\"48.5 \",Success" +
                    System.lineSeparator() +
                    "7,CASC,\"LC-101, Wenchang Satellite Launch Center, China\",\"Thu Jul 23, 2020\"" +
                    ",Long March 5 | Tianwen-1,StatusActive,,Success" +
                    System.lineSeparator() +
                    "8,SpaceX,\"SLC-40, Cape Canaveral AFS, Florida, USA\",\"Mon Jul 20, 2020\"" +
                    ",Falcon 9 Block 5 | ANASIS-II,StatusActive,\"50.0 \",Success" +
                    System.lineSeparator() +
                    "9,JAXA,\"LA-Y1, Tanegashima Space Center, Japan\",\"Sun Jul 19, 2020\"" +
                    ",H-IIA 202 | Hope Mars Mission,StatusActive,\"90.0 \",Success" +
                    System.lineSeparator() +
                    "10,Northrop,\"LP-0B, Wallops Flight Facility, Virginia, USA\",\"Wed Jul 15, 2020\"" +
                    ",Minotaur IV | NROL-129,StatusActive,\"46.0 \",Success" +
                    System.lineSeparator() +
                    "11,ExPace,\"Site 95, Jiuquan Satellite Launch Center, China\",\"Fri Jul 10, 2020\"" +
                    ",\"Kuaizhou 11 | Jilin-1 02E, CentiSpace-1 S2\",StatusActive,\"28.3 \",Failure" +
                    System.lineSeparator() +
                    "12,CASC,\"LC-3, Xichang Satellite Launch Center, China\",\"Thu Jul 09, 2020\"" +
                    ",Long March 3B/E | Apstar-6D,StatusActive,\"29.15 \",Success" +
                    System.lineSeparator() +
                    "13,IAI,\"Pad 1, Palmachim Airbase, Israel\",\"Mon Jul 06, 2020\"" +
                    ",Shavit-2 | Ofek-16,StatusActive,,Success" +
                    System.lineSeparator() +
                    "14,CASC,\"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China\",\"Sat Jul 04, 2020\"" +
                    ",Long March 2D | Shiyan-6 02,StatusActive,\"29.75 \",Success" +
                    System.lineSeparator() +
                    "15,Rocket Lab,\"Rocket Lab LC-1A, M?hia Peninsula, New Zealand\",\"Sat Jul 04, 2020\"" +
                    ",Electron/Curie | Pics Or It Didn??¦t Happen,StatusActive,\"7.5 \",Failure" +
                    System.lineSeparator() +
                    "16,CASC,\"LC-9, Taiyuan Satellite Launch Center, China\",\"Fri Jul 03, 2020\"" +
                    ",Long March 4B | Gaofen Duomo & BY-02,StatusActive,\"64.68 \",Success" + System.lineSeparator();


    private static final String ROCKET_DATA = "\"\",Name,Wiki,Rocket Height"
            + System.lineSeparator() + "169,Falcon 9 Block 5,https://en.wikipedia.org/wiki/Falcon_9,70.0 m"
            + System.lineSeparator() + "213,Long March 2D,https://en.wikipedia.org/wiki/Long_March_2D,41.06 m"
            + System.lineSeparator() + "371,Starship Prototype,https://en.wikipedia.org/wiki/SpaceX_Starship,50.0 m"
            + System.lineSeparator() + "294,Proton-M/Briz-M,https://en.wikipedia.org/wiki/Proton-M,58.2 m"
            + System.lineSeparator() + "103,Atlas V 541,https://en.wikipedia.org/wiki/Atlas_V,62.2 m"
            + System.lineSeparator() + "228,Long March 4B,https://en.wikipedia.org/wiki/Long_March_4B,44.1 m"
            + System.lineSeparator() + "337,Soyuz 2.1a,https://en.wikipedia.org/wiki/Soyuz-2,"
            + System.lineSeparator() + "230,Long March 5,https://en.wikipedia.org/wiki/Long_March_5,57.0 m"
            + System.lineSeparator() + "182,H-IIA 202,https://en.wikipedia.org/wiki/H-IIA,53.0 m"
            + System.lineSeparator() + "243,Minotaur IV,https://en.wikipedia.org/wiki/Minotaur_IV,23.88 m"
            + System.lineSeparator() + "200,Kuaizhou 11,https://en.wikipedia.org/wiki/Kuaizhou,25.0 m"
            + System.lineSeparator() + "222,Long March 3B/E,https://en.wikipedia.org/wiki/Long_March_3B,56.3 m"
            + System.lineSeparator() + "330,Shavit-2,https://en.wikipedia.org/wiki/Shavit,22.0 m"
            + System.lineSeparator() + "156,Electron/Curie,https://en.wikipedia.org/wiki/Electron_(rocket),17.0 m"
            + System.lineSeparator() + "228,Long March 4B,https://en.wikipedia.org/wiki/Long_March_4B,44.1 m"
            + System.lineSeparator();

    private static MJTSpaceScanner spaceScanner;

    @BeforeAll
    public static void setup() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(KEY_SIZE);
            SecretKey secretKey = keyGenerator.generateKey();

            spaceScanner = new MJTSpaceScanner(
                    new StringReader(CSV_DATA),
                    new StringReader(ROCKET_DATA),
                    secretKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating secret key or encrypting data", e);
        }
    }

    @Test
    public void testParsing() {
        assertEquals(CSV_DATA.split(System.lineSeparator()).length - 1,
                spaceScanner.getAllMissions().size());
        assertEquals(ROCKET_DATA.split(System.lineSeparator()).length - 1,
                spaceScanner.getAllRockets().size());

    }

    @Test
    public void testGetMissionsWithStatus() {
        assertEquals(2, spaceScanner.getAllMissions(MissionStatus.FAILURE).size());
        assertEquals(spaceScanner.getAllMissions().size() - 2,
                spaceScanner.getAllMissions(MissionStatus.SUCCESS).size());
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getAllMissions(null));
    }

    @Test
    public void testGetCompanyWithMostSuccessfulMissions() {
        assertEquals("CASC", spaceScanner.getCompanyWithMostSuccessfulMissions(
                LocalDate.now().minusYears(5), LocalDate.now().minusYears(1)));
        assertThrows(IllegalArgumentException.class, () -> spaceScanner
                .getCompanyWithMostSuccessfulMissions(null, null));
        assertThrows(TimeFrameMismatchException.class, () -> spaceScanner
                .getCompanyWithMostSuccessfulMissions(LocalDate.now(), LocalDate.now().minusYears(1)));
    }

    @Test
    public void testGetMissionsPerCountry() {
        Map<String, Collection<Mission>> missionsPerCountry = spaceScanner.getMissionsPerCountry();

        assertEquals(5, missionsPerCountry.get("USA").size());
        assertEquals(2, missionsPerCountry.get("Kazakhstan").size());
    }

    @Test
    public void testGetLocationWithMostSuccessfulMissionsPerCompany() {
        Map<String, String> result = spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(
                LocalDate.now().minusYears(5), LocalDate.now().minusYears(1));

        assertEquals("LA-Y1, Tanegashima Space Center, Japan", result.get("JAXA"));
        assertThrows(IllegalArgumentException.class, () -> spaceScanner
                .getLocationWithMostSuccessfulMissionsPerCompany(null, null));
        assertThrows(TimeFrameMismatchException.class, () -> spaceScanner
                .getLocationWithMostSuccessfulMissionsPerCompany(LocalDate.now(), LocalDate.now().minusYears(1)));
    }

    @Test
    public void testGetTopNLeastExpensiveMissions() {
        List<Mission> missions = spaceScanner
                .getTopNLeastExpensiveMissions(2, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);
        List<Mission> expected = List.of(
                Mission.of("12,CASC,\"LC-3, Xichang Satellite Launch Center, China\",\"Thu Jul 09, 2020\"" +
                ",Long March 3B/E | Apstar-6D,StatusActive,\"29.15 \",Success"),
                Mission.of("1,CASC,\"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China\",\"Thu Aug 06, 2020\"" +
                        ",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,\"29.75 \",Success"));
        assertEquals(expected, missions);
        assertThrows(IllegalArgumentException.class, () -> spaceScanner
                .getTopNLeastExpensiveMissions(0, null, null));
    }

    private InputStream encryptData(InputStream inputStream, SecretKey secretKey) {
        try (ByteArrayOutputStream encryptedStream = new ByteArrayOutputStream()) {
            Rijndael rijndael = new Rijndael(secretKey);
            rijndael.encrypt(inputStream, encryptedStream);
            return new ByteArrayInputStream(encryptedStream.toByteArray());
        } catch (IOException | CipherException e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

}