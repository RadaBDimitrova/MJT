package bg.sofia.uni.fmi.mjt.udemy.account;

import bg.sofia.uni.fmi.mjt.udemy.account.type.AccountType;
import bg.sofia.uni.fmi.mjt.udemy.course.Category;
import bg.sofia.uni.fmi.mjt.udemy.course.Course;
import bg.sofia.uni.fmi.mjt.udemy.exception.CourseAlreadyPurchasedException;
import bg.sofia.uni.fmi.mjt.udemy.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.udemy.exception.MaxCourseCapacityReachedException;

public class BusinessAccount extends AccountBase {
    private Category[] allowedCategories;
    public BusinessAccount(String username, double balance, Category[] allowedCategories){
        super(username, balance);
        this.allowedCategories = new Category[allowedCategories.length];
        System.arraycopy(allowedCategories, 0, this.allowedCategories, 0, allowedCategories.length);
        type = AccountType.BUSINESS;
    }

    @Override
    public void buyCourse(Course course) throws InsufficientBalanceException, CourseAlreadyPurchasedException, MaxCourseCapacityReachedException {
        boolean contains = false;
        for (Category allowedCategory : allowedCategories) {
            if (course.getCategory().equals(allowedCategory)) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            throw new IllegalArgumentException("Buying course failed. Category not permitted for business, account:" + getUsername());
        }
        if (course.getPrice() > balance) {
            throw new InsufficientBalanceException("Buying course failed. Insufficient balance, account:" + username);
        }
        if (boughtCourses >= MAX_COURSES) {
            throw new MaxCourseCapacityReachedException("Buying course failed. Max courses reached, account:" + username);
        }
        for (int i = 0; i < boughtCourses; i++) {
            if (course.equals(courses[i])) {
                throw new CourseAlreadyPurchasedException("Buying course failed. Course already purchased, account:" + username);
            }
        }
        courses[boughtCourses] = course;
        courses[boughtCourses++].purchase();
        balance -= course.getPrice();
        balance+= course.getPrice()* type.getDiscount();
    }
}
