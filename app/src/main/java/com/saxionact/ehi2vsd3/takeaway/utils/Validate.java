package com.saxionact.ehi2vsd3.takeaway.utils;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.saxionact.ehi2vsd3.takeaway.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides static method's which you can use to validate the input from users.
 *
 * @author Vincent Witten
 */
public abstract class Validate {

    //Patterns and matcher
    private static final String FULLNAME_PATTERN = "^[\\p{L} .'-]+$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";

    private static Pattern pattern;
    private static Matcher matcher;

    //Method validation types
    public static Method FULLNAME;
    public static Method EMAIL;

    static {
        try {
            FULLNAME = Validate.class.getMethod("validateFullName", String.class);
            EMAIL = Validate.class.getMethod("validateEmail", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * form validator
     * <p>
     * return true if a form has been validated as correct and displays
     * the error message if the the form has been validated as incorrect.
     * </p>
     *
     * @param context  contains the context for the string resource.
     * @param textView contains the textview where the error message gets displayed.
     * @param editText contains the edittext which value gets validated.
     * @param method   contains the method reflection for the right validation type.
     * @param message  contains the message thats gets displayed when the form has been validated as incorrect.
     * @return returns true if the validation of the form has been validated as correct.
     * @author Vincent Witten
     */
    public static boolean validate(Context context, View textView, View editText, Method method, int message) {
        TextView tv = (TextView) textView;
        EditText et = (EditText) editText;

        //checks if edittext is empty
        if (et.getText().toString().equals("")) {
            textView.setVisibility(View.VISIBLE);
            tv.setText(String.format(context.getString(R.string.empty_field), String.valueOf(et.getHint())));
            return false;
        } else {
            //checks if the edittext value is valid
            try {
                if (!(boolean) method.invoke(null, et.getText().toString())) {
                    textView.setVisibility(View.VISIBLE);
                    tv.setText(message);
                    return false;
                } else {
                    textView.setVisibility(View.GONE);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * This method checks if there are no weird symbols that don't belong in a person's name.
     *
     * @param fullName the name of the user provided for validation.
     * @return boolean
     * @author Vincent Witten
     */
    public static boolean validateFullName(String fullName) {
        pattern = Pattern.compile(FULLNAME_PATTERN);
        matcher = pattern.matcher(fullName);
        return matcher.matches();
    }

    /**
     * This method checks if the provided email address is correct.
     *
     * @param email the email address of the user provided for validation.
     * @return boolean
     * @author Vincent Witten
     */
    public static boolean validateEmail(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
