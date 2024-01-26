package bg.sofia.uni.fmi.mjt.gym.member;

import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Member implements GymMember, Comparable {
    protected String name;
    protected int age;
    protected String personalIdNumber;
    protected Gender gender;
    protected Address address;
    protected HashMap<DayOfWeek, Workout> trainingProgram;

    public int compare(Member o1, Member o2) {
        return o1.getName().compareTo(o2.getName());
    }

    public Member(Address address, String name, int age, String personalIdNumber, Gender gender) {
        this.address = address;
        this.name = name;
        this.age = age;
        this.personalIdNumber = personalIdNumber;
        this.gender = gender;
        this.trainingProgram = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public String getPersonalIdNumber() {
        return personalIdNumber;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Map<DayOfWeek, Workout> getTrainingProgram() {
        return Collections.unmodifiableMap(trainingProgram);
    }

    @Override
    public void setWorkout(DayOfWeek day, Workout workout) {
        if (trainingProgram == null || workout == null || day == null) {
            throw new IllegalArgumentException("Null training program or workout");
        }
        trainingProgram.put(day, workout);
    }

    @Override
    public Collection<DayOfWeek> getDaysFinishingWith(String exerciseName) {
        if (exerciseName == null || exerciseName.isEmpty()) {
            throw new IllegalArgumentException("Empty string for exercise, cannot get days ending.");
        }
        Collection<DayOfWeek> arr = new HashSet<DayOfWeek>();
        for (Map.Entry<DayOfWeek, Workout> entry : trainingProgram.entrySet()) {
            DayOfWeek key = entry.getKey();
            Workout value = entry.getValue();
            if (!value.exercises().isEmpty() && value.exercises().getLast().name().equals(exerciseName)) {
                arr.add(key);
            }
        }
        return arr;
    }

    @Override
    public void addExercise(DayOfWeek day, Exercise exercise) {
        if (exercise == null || day == null) {
            throw new IllegalArgumentException("Null exercise.");
        }
        Workout workout = trainingProgram.get(day);
        if (workout == null || workout.exercises() == null) {
            throw new DayOffException("Day off.");
        }
        for (Map.Entry<DayOfWeek, Workout> entry : trainingProgram.entrySet()) {
            if (entry == null) {
                throw new DayOffException("Day off.");
            }
            DayOfWeek key = entry.getKey();
            Workout value = entry.getValue();
            if (value == null) {
                throw new DayOffException("Day off.");
            }
        }
        trainingProgram.get(day).exercises().add(exercise);
    }

    @Override
    public void addExercises(DayOfWeek day, List<Exercise> exercises) {
        if (exercises == null || day == null || exercises.isEmpty()) {
            throw new IllegalArgumentException("Null exercises or day.");
        }
        Workout workout = trainingProgram.get(day);
        if (workout == null || workout.exercises() == null) {
            throw new DayOffException("Day off.");
        }
        for (Exercise exercise : exercises) {
            if (exercise == null) {
                throw new IllegalArgumentException("Null exercise.");
            }
            addExercise(day, exercise);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(personalIdNumber);
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Member)) {
            throw new ClassCastException("Not an instance of Member");
        }
        if (this.equals(o)) {
            return 0;
        }
        return this.hashCode() > o.hashCode() ? 1 : -1;
    }
}


