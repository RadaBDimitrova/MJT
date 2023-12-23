package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {

    private List<Mission> missions;
    private List<Rocket> rockets;
    private final Rijndael rijndael;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        rijndael = new Rijndael(secretKey);
        missions = new ArrayList<>();
        rockets = new ArrayList<>();

        try (ByteArrayOutputStream missionsOs = new ByteArrayOutputStream();
             ByteArrayOutputStream rocketsOs = new ByteArrayOutputStream()) {

            String missionsContent = readerToString(missionsReader);
            String rocketsContent = readerToString(rocketsReader);

            InputStream missionsStream = new ByteArrayInputStream(missionsContent.getBytes(StandardCharsets.UTF_16));
            InputStream rocketsStream = new ByteArrayInputStream(rocketsContent.getBytes(StandardCharsets.UTF_16));

            rijndael.decrypt(missionsStream, missionsOs);
            rijndael.decrypt(rocketsStream, rocketsOs);

            InputStream missionsBufferedReader = new ByteArrayInputStream(missionsOs.toByteArray());
            InputStream rocketsBufferedReader = new ByteArrayInputStream(rocketsOs.toByteArray());

            missionParser(missionsBufferedReader); //throws cipherException
            rocketsParser(rocketsBufferedReader);

        } catch (IOException e) {
            throw new UncheckedIOException("Error while setting up in constructor", e);
        } catch (CipherException e) {
            throw new RuntimeException("Error during decryption", e);
        }
    }

    private String readerToString(Reader reader) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString();
        }
    }

    private void missionParser(InputStream bufferedReader) {
        int ctr = 1;
        String line;
        try (var reader = new BufferedReader(new InputStreamReader(bufferedReader))) {
            while ((line = reader.readLine()) != null) {
                if (ctr != 1) {
                    Mission current = Mission.of(line);
                    missions.add(current);
                }
                ctr++;
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading CSV data", e);
        }
    }

    private void rocketsParser(InputStream bufferedReader) {
        int ctr = 1;
        String line;
        try (var reader = new BufferedReader(new InputStreamReader(bufferedReader))) {
            while ((line = reader.readLine()) != null) {
                if (ctr != 1) {
                    Rocket current = Rocket.of(line);
                    rockets.add(current);
                }
                ctr++;
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading CSV data", e);
        }
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return missions;
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission status cannot be null.");
        }
        return missions.stream()
                .filter(mission -> mission.missionStatus() == missionStatus)
                .collect(Collectors.toList());
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (from.isAfter(to) || to.isBefore(from)) {
            throw new TimeFrameMismatchException("Invalid time interval");
        }
        Map<String, Long> companySuccessCount = missions.stream()
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS &&
                        mission.date().isAfter(from) && mission.date().isBefore(to))
                .collect(Collectors.groupingBy(Mission::company, Collectors.counting()));

        Optional<Map.Entry<String, Long>> entry = companySuccessCount.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        return entry.map(Map.Entry::getKey).orElse("");
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream()
                .collect(Collectors.groupingBy(Mission::location, Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus
            rocketStatus) {
        if (n <= 0 || missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        return missions.stream()
                .filter(mission -> mission.missionStatus() == missionStatus)
                .sorted(Comparator.comparing(mission -> mission.cost().orElse(Double.MAX_VALUE)))
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        return missions.stream()
                .collect(Collectors.groupingBy(Mission::company, Collectors.collectingAndThen(
                        Collectors.groupingBy(Mission::location, Collectors.counting()),
                        map -> map.entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse(""))));
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (from.isAfter(to) || to.isBefore(from)) {
            throw new TimeFrameMismatchException("Invalid time interval");
        }
        return missions.stream()
                .filter(mission -> mission.missionStatus() == MissionStatus.SUCCESS &&
                        mission.date().isAfter(from) && mission.date().isBefore(to))
                .collect(Collectors.groupingBy(Mission::company, Collectors.collectingAndThen(
                        Collectors.groupingBy(Mission::location, Collectors.counting()),
                        map -> map.entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse(""))));
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return rockets;
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("N cannot be negative");
        }
        return rockets.stream()
                .sorted(Comparator.comparing(rocket -> rocket.height().orElse(Double.MIN_VALUE)))
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets.stream()
                .collect(Collectors.toMap(Rocket::name, Rocket::wiki));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus
            missionStatus, RocketStatus rocketStatus) {
        if (n <= 0 || missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        return missions.stream()
                .filter(mission -> mission.missionStatus() == missionStatus)
                .sorted(Comparator.comparing(mission -> mission.cost().orElse(Double.MIN_VALUE),
                        Comparator.reverseOrder()))
                .limit(n)
                .map(mission -> rockets.stream()
                        .filter(rocket -> rocket.name().equals(mission.detail().rocketName()))
                        .findFirst()
                        .map(rocket -> rocket.wiki().orElse(""))
                        .orElse(""))
                .collect(Collectors.toList());
    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {
        if (outputStream == null || from == null || to == null) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        if (from.isAfter(to) || to.isBefore(from)) {
            throw new TimeFrameMismatchException("Invalid time interval");
        }

        Optional<Rocket> mostReliableRocket = getAllRockets().stream()
                .max(Comparator.comparingDouble(rocket -> calculateReliability(rocket, from, to)));

        if (mostReliableRocket.isPresent()) {
            try {
                rijndael.encrypt(new ByteArrayInputStream(mostReliableRocket.toString()
                        .getBytes(StandardCharsets.UTF_16)), outputStream);
            } catch (CipherException e) {
                throw new CipherException("Encryption failed", e);
            }
        }
    }

    private double calculateReliability(Rocket rocket, LocalDate from, LocalDate to) {
        double totalMissions = getAllMissions().stream()
                .filter(mission -> mission.detail().rocketName().equals(rocket.name()))
                .count();

        double successfulMissions = getAllMissions().stream()
                .filter(mission -> mission.detail().rocketName().equals(rocket.name())
                        && mission.missionStatus() == MissionStatus.SUCCESS
                        && mission.date().isAfter(from)
                        && mission.date().isBefore(to))
                .count();
        double unsuccessfulMissions = totalMissions - successfulMissions;
        return totalMissions == 0 ? 0 : ((2.0 * successfulMissions) + unsuccessfulMissions) / (2.0 * totalMissions);
    }
}
