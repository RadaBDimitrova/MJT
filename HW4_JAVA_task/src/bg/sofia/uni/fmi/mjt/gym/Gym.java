package bg.sofia.uni.fmi.mjt.gym;

import bg.sofia.uni.fmi.mjt.gym.member.Address;
import bg.sofia.uni.fmi.mjt.gym.member.GymMember;
import bg.sofia.uni.fmi.mjt.gym.member.NameComparator;
import bg.sofia.uni.fmi.mjt.gym.member.ProximityComparator;
import bg.sofia.uni.fmi.mjt.gym.workout.Exercise;
import bg.sofia.uni.fmi.mjt.gym.workout.Workout;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Gym implements GymAPI {
    private SortedSet<GymMember> members;
    private Address address;
    private int capacity;

    public Gym(int capacity, Address address) {
        this.members = new TreeSet<GymMember>();
        this.address = address;
        this.capacity = capacity;
    }

    @Override
    public SortedSet<GymMember> getMembers() {
        return Collections.unmodifiableSortedSet(members);
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByName() {
        SortedSet<GymMember> result = new TreeSet<>(new NameComparator());
        result.addAll(members);
        return Collections.unmodifiableSortedSet(result);
    }

    @Override
    public SortedSet<GymMember> getMembersSortedByProximityToGym() {
        SortedSet<GymMember> result = new TreeSet<>(new ProximityComparator(address));
        result.addAll(members);
        return Collections.unmodifiableSortedSet(result);
    }

    @Override
    public void addMember(GymMember member) throws GymCapacityExceededException {
        if (member == null) {
            throw new IllegalArgumentException("Member is null");
        }
        if (members.size() == capacity) {
            throw new GymCapacityExceededException("Capacity exceeded.");
        }
        members.add(member);
    }

    @Override
    public void addMembers(Collection<GymMember> members) throws GymCapacityExceededException {
        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("Members to add is null or empty");
        }
        if (members.size() >= capacity) {
            throw new GymCapacityExceededException("Capacity exceeded.");
        }
        this.members.addAll(members);
    }

    @Override
    public boolean isMember(GymMember member) {
        if (member == null) {
            throw new IllegalArgumentException("Member is null.");
        }
        return members.contains(member);
    }

    @Override
    public boolean isExerciseTrainedOnDay(String exerciseName, DayOfWeek day) {
        if (exerciseName == null || exerciseName.isEmpty() || day == null) {
            throw new IllegalArgumentException("Name or day is null/empty");
        }
        for (GymMember member : members) {
            Workout workout = member.getTrainingProgram().get(day);
            if (workout != null) {
                for (Exercise exercise : workout.exercises()) {
                    if (exercise.name().equals(exerciseName)) {
                        return true;
                    }
                }
            } else {
                throw new IllegalArgumentException("Day has a null workout.");
            }
        }
        return false;
    }

    @Override
    public Map<DayOfWeek, List<String>> getDailyListOfMembersForExercise(String exerciseName) {
        if (exerciseName == null || exerciseName.isEmpty()) {
            throw new IllegalArgumentException("Name is null or empty");
        }
        Map<DayOfWeek, List<String>> dailyListOfMembers = new HashMap<>();
        for (GymMember member : members) {
            for (Map.Entry<DayOfWeek, Workout> entry : member.getTrainingProgram().entrySet()) {
                DayOfWeek day = entry.getKey();
                Workout workout = entry.getValue();
                for (Exercise exercise : workout.exercises()) {
                    if (exercise.name().equals(exerciseName)) {
                        List<String> membersList = dailyListOfMembers.computeIfAbsent(day, k -> new ArrayList<>());
                        membersList.add(member.getName());
                    }
                }
            }
        }
        return dailyListOfMembers;
    }
}
