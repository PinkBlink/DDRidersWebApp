function isPasswordsEquals(password1, password2){

    return password1 === password2;
}
function checkPasswords() {
    const password1 = document.getElementById('password1').value;
    const password2 = document.getElementById('password2').value;
    const errorMessage = document.getElementById('error-message');

    if (!isPasswordsEquals(password1, password2)) {
        errorMessage.textContent = "Passwords do not match.";
    } else {
        errorMessage.textContent = "";
    }
}