package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotCompletedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseNotPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.ResourceNotFoundException;

public abstract class AccountBase implements Account {
    private String username;
    private double balance;
    private Course[] courses;
    private int boughtCourses;
    private static final int MAX_COURSES = 100;
    private AccountType type;

    public int getIdxOfCourse(Course course){
        for (int i = 0; i < courses.length; i++) {
            if(courses[i].isEqual(course)){
                return i;
            }
        }
        return -1;
    }

    public AccountBase(String username, double balance) {
        this.username = username;
        this.balance = balance;
        this.courses = new Course[MAX_COURSES];
    }

    public void addToBalance(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        balance += amount;
    }

    public double getBalance() {
        return balance;
    }

    public String getUsername() {
        return username;
    }
    public abstract void completeCourse(Course course, double grade) throws CourseNotPurchasedException, CourseNotCompletedException;
}
