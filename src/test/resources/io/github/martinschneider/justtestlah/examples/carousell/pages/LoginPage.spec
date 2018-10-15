@objects
    username_field    xpath    //TextInputLayout[@resource-id='com.thecarousell.Carousell:id/login_page_username_text_field']//android.widget.EditText
    password_field    xpath    //TextInputLayout[@resource-id='com.thecarousell.Carousell:id/login_page_password_text_field']//android.widget.EditText
    login_button      id       com.thecarousell.Carousell:id/login_page_login_button

= Login =

    username_field:
        above password_field
        aligned vertically all password_field
        width 100 % of password_field/width
        width 100 % of login_button/width
	
    password_field:
        below username_field
        aligned vertically all username_field
        width 100 % of username_field/width
        width 100 % of login_button/width
        
    login_button:
        below password_field
        below username_field
        width 100 % of username_field/width
        width 100 % of password_field/width
        text is "Log In" 