package bg.sofia.uni.fmi.mjt.football;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public record Player(String name, String fullName, LocalDate birthDate, int age, double heightCm, double weightKg,
                     List<Position> positions, String nationality, int overallRating, int potential, long valueEuro,
                     long wageEuro, Foot preferredFoot) {

    public static Player of(String line) {
        String[] arr = line.split("\\Q" + ";" + "\\E");
        int i = 0;
        String name = arr[i++];
        String fullName = arr[i++];
        String[] date = arr[i++].split("\\Q" + "/" + "\\E");
        LocalDate birthDate = LocalDate.of(Integer.parseInt(date[2]),
                Integer.parseInt(date[0]), Integer.parseInt(date[1]));
        int age = Integer.parseInt(arr[i++]);
        double heightCm = Double.parseDouble((arr[i++]));
        double weightKg = Double.parseDouble((arr[i++]));
        List<String> positionsNames = List.of(arr[i++].split("\\Q" + "," + "\\E"));
        List<Position> positions = new LinkedList<>();
        if (!positionsNames.isEmpty()) {
            for (String position : positionsNames) {
                positions.add(Position.valueOf(position));
            }
        }
        String nationality = arr[i++];
        int overallRating = Integer.parseInt(arr[i++]);
        int potential = Integer.parseInt(arr[i++]);
        long valueEuro = Long.parseLong(arr[i++]);
        long wageEuro = Long.parseLong(arr[i++]);
        Foot preferredFoot = Foot.valueOf(arr[i++].toUpperCase());
        return new Player(name, fullName, birthDate, age, heightCm, weightKg,
                positions, nationality, overallRating, potential, valueEuro, wageEuro, preferredFoot);
    }
}

