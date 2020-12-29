package com.company;

public class Teacher {
    String name;
    String job;
    int salary;
    public Teacher(){}
    public Teacher(String name, String job, int salary) {
        this.name = name;
        this.job = job;
        this.salary = salary;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getJob() { return job; }
    public void setJob(String job) { this.job = job; }
    public int getSalary() { return salary; }
    public void setSalary(int salary) { this.salary = salary; }
}
