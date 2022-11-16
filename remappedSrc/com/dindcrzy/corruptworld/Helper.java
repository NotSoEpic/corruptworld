package com.dindcrzy.corruptworld;

public class Helper {
    public static int modulo(int a, int b) {
        return ((a % b) + b) % b;
    }
    public static double modulo(double a, double b) {
        return ((a % b) + b) % b;
    }
}
