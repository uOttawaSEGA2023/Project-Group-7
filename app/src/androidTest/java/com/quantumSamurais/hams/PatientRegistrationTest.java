package com.quantumSamurais.hams;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.quantumSamurais.hams.utils.Validator.nameIsValid;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.quantumSamurais.hams.patient.activities.PatientSignUpActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PatientRegistrationTest {

    @Rule
    public ActivityScenarioRule<PatientSignUpActivity> activityRule = new ActivityScenarioRule<>(PatientSignUpActivity.class);


    @Test
    public void testNameIsValid_withValidName_returnsTrue() {
        assertTrue(nameIsValid("John Doe"));
    }

    @Test
    public void testNameIsValid_withInvalidName_returnsFalse() {
        assertFalse(nameIsValid("John123"));
    }

    @Test
    public void AllFieldsAreEmpty() {
        onView(withId(R.id.formSignUpButton)).perform(click());
        onView(withText("Please make sure to fill all the fields."))
                .check(matches(isDisplayed()));
    }

    @Test
    public void someFieldsAreEmpty() {
        onView(withId(R.id.firstNameSlot)).perform(typeText("John"), closeSoftKeyboard());
        onView(withId(R.id.formSignUpButton)).perform(click());
        onView(withText("Please make sure to fill all the fields."))
                .check(matches(isDisplayed()));
    }

    @Test
    public void emailFormatIsInvalid() {
        onView(withId(R.id.firstNameSlot)).perform(typeText("John"), closeSoftKeyboard());
        onView(withId(R.id.lastNameSlot)).perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.passwordSlot)).perform(typeText("Crimson7!"), closeSoftKeyboard());
        onView(withId(R.id.emailAddressSlot)).perform(typeText("ange@"), closeSoftKeyboard());
        onView(withId(R.id.phoneNumberSlot)).perform(typeText("1112224440"), closeSoftKeyboard());
        onView(withId(R.id.postalAddressSlot)).perform(typeText("asda"), closeSoftKeyboard());
        onView(withId(R.id.healthCardNumberSlot)).perform(typeText("asda111"), closeSoftKeyboard());
        onView(withId(R.id.formSignUpButton)).perform(click());
        onView(withText("This email address is not formatted like an email address."))
                .check(matches(isDisplayed()));
    }

    @Test
    public void emailDomainIsInvalid() {
        onView(withId(R.id.firstNameSlot)).perform(typeText("John"), closeSoftKeyboard());
        onView(withId(R.id.lastNameSlot)).perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.passwordSlot)).perform(typeText("Crimson7!"), closeSoftKeyboard());
        onView(withId(R.id.emailAddressSlot)).perform(typeText("ange@bismila.xz"), closeSoftKeyboard());
        onView(withId(R.id.phoneNumberSlot)).perform(typeText("1112224440"), closeSoftKeyboard());
        onView(withId(R.id.postalAddressSlot)).perform(typeText("asda"), closeSoftKeyboard());
        onView(withId(R.id.healthCardNumberSlot)).perform(typeText("asda111"), closeSoftKeyboard());
        onView(withId(R.id.formSignUpButton)).perform(click());
        onView(withText("Please ensure this email address' domain exists."))
                .check(matches(isDisplayed()));
    }

    @Test
    public void emailLocalDomainIsInvalid() {
        onView(withId(R.id.firstNameSlot)).perform(typeText("John"), closeSoftKeyboard());
        onView(withId(R.id.lastNameSlot)).perform(typeText("Doe"), closeSoftKeyboard());
        onView(withId(R.id.passwordSlot)).perform(typeText("Crimson7!"), closeSoftKeyboard());
        onView(withId(R.id.emailAddressSlot)).perform(typeText("an g e@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.phoneNumberSlot)).perform(typeText("1112224440"), closeSoftKeyboard());
        onView(withId(R.id.postalAddressSlot)).perform(typeText("asda"), closeSoftKeyboard());
        onView(withId(R.id.healthCardNumberSlot)).perform(typeText("asda111"), closeSoftKeyboard());
        onView(withId(R.id.formSignUpButton)).perform(click());
        onView(withText("Please ensure the localPart of your email address is correct, ensure there are no spaces."))
                .check(matches(isDisplayed()));
    }

}
