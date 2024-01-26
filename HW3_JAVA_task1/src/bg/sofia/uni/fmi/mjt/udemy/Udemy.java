package bg.sofia.uni.fmi.mjt.udemy;

import bg.sofia.uni.fmi.mjt.udemy.account.Account;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotFoundException;

import java.util.Arrays;

public class Udemy implements LearningPlatform {
    Account[] accounts;
    Course[] courses;

    public Udemy(Account[] accounts, Course[] courses) {
        this.accounts = new Account[accounts.length];
        System.arraycopy(accounts, 0, this.accounts, 0, accounts.length);
        this.courses = new Course[courses.length];
        System.arraycopy(courses, 0, this.courses, 0, courses.length);
    }

    @Override
    public Course findByName(String name) throws CourseNotFoundException {
        if (name != null && !name.isBlank() && !name.isEmpty()) {
            for (Course course : courses) {
                if (course.getName().equals(name)) {
                    return course;
                }
            }
        }
        throw new CourseNotFoundException("Course not found.");
    }

    @Override
    public Course[] findByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid keyword.");
        }
        Course[] arr = new Course[courses.length];
        int ctr = 0;
        for (Course course : courses) {
            if (course.getName().contains(keyword) || course.getDescription().contains(keyword)) {
                arr[ctr++] = course;
            }
        }
        return arr;
    }

    @Override
    public Course[] getAllCoursesByCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        Course[] arr = new Course[courses.length];
        int ctr = 0;
        for (Course course : courses) {
            if (course.getCategory().equals(category)) {
                arr[ctr++] = course;
            }
        }
        return arr;
    }

    @Override
    public Account getAccount(String name) throws AccountNotFoundException {
        if (name != null && !name.isBlank() && !name.isEmpty()) {
            throw new AccountNotFoundException("Account not found, error with string.");
        }
        for (Account account : accounts) {
            if (account.getUsername().equals(name)) {
                return account;
            }
        }
        throw new AccountNotFoundException("Account not found:" + name);
    }

    @Override
    public Course getLongestCourse() {
        if (courses.length == 0) {
            return null;
        }
        Course max = courses[0];
        for (Course course : courses) {
            if (course.getTotalTime().hours() > max.getTotalTime().hours()) {
                max = course;
            } else if (course.getTotalTime().hours() == max.getTotalTime().hours() &&
                    course.getTotalTime().minutes() > max.getTotalTime().minutes()) {
                max = course;
            }
        }
        return max;
    }

    @Override
    public Course getCheapestByCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category is null.");
        }
        Course[] arr = getAllCoursesByCategory(category);
        Course min = arr[0];
        for (Course course : arr) {
            if (course.getPrice() < min.getPrice()) {
                min = course;
            }
        }
        return min;
    }
}
