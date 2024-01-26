package bg.sofia.uni.fmi.mjt.gym.workout;

import static java.util.Objects.hash;

public record Exercise(String name, int sets, int repetitions) implements Comparable {
    public Exercise {
        if (name == null || sets < 0 || repetitions < 0) {
            throw new IllegalArgumentException("Invalid name, number of sets or number of repetitions");
        }
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Exercise)) {
            throw new ClassCastException("Not an instance of Exercise");
        }
        if (this.equals(o)) {
            return 0;
        }
        return this.hashCode() > o.hashCode() ? 1 : -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Exercise other = (Exercise) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return hash(name);
    }
}
