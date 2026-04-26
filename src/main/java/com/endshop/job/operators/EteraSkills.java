package com.endshop.job.operators;
import com.endshop.job.profession.Profession;
public class EteraSkills {
    public static void register() {}
    public static Profession getProfession() { return EteraProfile.PROFESSION; }
    public static String[] getSkills() { return EteraProfile.SKILLS; }
}
