package bg.sofia.uni.fmi.mjt.gym.workout;

import java.util.SequencedCollection;

public record Workout(SequencedCollection<Exercise> exercises) {
    public Workout {
        if (exercises == null) {
            throw new IllegalArgumentException("Null collection.");
        }
        for (Exercise exercise : exercises) {
            if (exercise.name() == null || exercise.sets() < 0 || exercise.repetitions() < 0) {
                throw new IllegalArgumentException("Invalid name, number of sets or number of repetitions");
            }
        }
    }

}
